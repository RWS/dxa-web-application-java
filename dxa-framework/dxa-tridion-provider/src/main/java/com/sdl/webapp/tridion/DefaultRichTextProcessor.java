package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.RichTextProcessor;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.RichTextFragment;
import com.sdl.webapp.common.api.model.RichTextFragmentImpl;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.NodeListAdapter;
import com.sdl.webapp.common.util.XMLUtils;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sdl.webapp.tridion.xpath.XPathResolver.XLINK_NS_URI;
import static com.sdl.webapp.tridion.xpath.XPathResolver.XPATH_IMAGES;
import static com.sdl.webapp.tridion.xpath.XPathResolver.XPATH_LINKS;
import static java.lang.Long.parseLong;
import static javax.xml.xpath.XPathConstants.NODESET;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
public class DefaultRichTextProcessor implements RichTextProcessor {

    public static final String EMBEDDED_ENTITY = "EmbeddedEntity";
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRichTextProcessor.class);
    @Autowired
    private MediaHelper mediaHelper;

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private TridionLinkResolver linkResolver;

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private ComponentPresentationFactory componentFactory;

    @Override
    public RichText processRichText(String xhtml, Localization localization) {
        try {
            // Parse the document as XML
            final Document document = XMLUtils.parse("<xhtml>" + xhtml + "</xhtml>");

            // Resolve links, images and YouTube videos
            return resolveRichText(document, localization);
        } catch (SAXException | IOException | ContentProviderException | SemanticMappingException e) {
            LOG.warn("Exception while parsing or processing XML content", e);
            return new RichText(xhtml);
        }
    }

    private <T extends ViewModel> T createInstance(Class<? extends T> entityClass) throws SemanticMappingException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("entityClass: {}", entityClass.getName());
        }
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SemanticMappingException("Exception while creating instance of entity class: " + entityClass.getName(), e);
        }
    }

    private RichText resolveRichText(Document doc, Localization localization)
            throws ContentProviderException, SemanticMappingException {
        this.resolveLinks(doc);
        List<EntityModel> entityModels = this.resolveImages(doc, localization);
        Iterator<EntityModel> embeddedEntities = entityModels != null ?
                entityModels.iterator() : Collections.<EntityModel>emptyListIterator();

        List<RichTextFragment> richTextFragments = new LinkedList<>();
        try {
            XMLUtils.format(doc);
            String xhtml = innerXml(doc.getFirstChild());

            int lastFragmentIndex = 0;

            Pattern pattern = Pattern.compile("<\\?EmbeddedEntity\\s?\\?>");
            Matcher matcher = pattern.matcher(xhtml);

            while (matcher.find()) {
                int embeddedEntityIndex = matcher.start();

                if (embeddedEntityIndex > lastFragmentIndex) {
                    richTextFragments.add(new RichTextFragmentImpl(xhtml.substring(lastFragmentIndex, embeddedEntityIndex)));
                }
                richTextFragments.add((RichTextFragment) embeddedEntities.next());
                lastFragmentIndex = matcher.end();
            }

            if (lastFragmentIndex < xhtml.length()) {
                // Final text fragment
                richTextFragments.add(new RichTextFragmentImpl(xhtml.substring(lastFragmentIndex)));
            }
        } catch (TransformerException e) {
            LOG.error("Exception while formatting XML for RichText", e);
        }

        return new RichText(richTextFragments);
    }

    private List<EntityModel> resolveImages(Document doc, Localization localization) throws ContentProviderException, SemanticMappingException {
        List<Node> entityElements;
        try {
            entityElements = new NodeListAdapter((NodeList) XPATH_IMAGES.expr().get().evaluate(doc, NODESET));
        } catch (XPathExpressionException e) {
            LOG.warn("Error while evaluation XPath expression", e);
            return null;
        }

        List<EntityModel> embeddedEntities = new LinkedList<>();
        for (Node imgElement : entityElements) {
            String[] schemaTcmUriParts = imgElement.getAttributes().getNamedItem("data-schemaUri").getNodeValue().split("-");
            final SemanticSchema semanticSchema = localization.getSemanticSchemas().get(parseLong(schemaTcmUriParts[1]));

            String viewName = semanticSchema.getRootElement();
            final Class<? extends ViewModel> entityClass;
            try {
                entityClass = viewModelRegistry.getMappedModelTypes(viewName);
                if (entityClass == null) {
                    throw new DxaException("Entity class is not registered in View Model Registry, null value");
                }
            } catch (DxaException e) {
                throw new ContentProviderException("Cannot determine entity type for view name: '" + viewName + "'. " +
                        "Please make sure that an entry is registered for this view name in the ViewModelRegistry.", e);
            }

            MediaItem mediaItem = (MediaItem) createInstance(entityClass);
            mediaItem.readFromXhtmlElement(imgElement);
            embeddedEntities.add(mediaItem);

            imgElement.getParentNode().replaceChild(doc.createProcessingInstruction(EMBEDDED_ENTITY, EMPTY), imgElement);
        }
        return embeddedEntities;
    }

    private void resolveLinks(Document document) {
        final List<Node> linkElements;
        try {
            linkElements = new NodeListAdapter((NodeList) XPATH_LINKS.expr().get().evaluate(document, NODESET));
        } catch (XPathExpressionException e) {
            LOG.warn("Error while evaluation XPath expression", e);
            return;
        }

        for (Node linkNode : linkElements) {
            final Element linkElement = (Element) linkNode;

            // Check if this link already has a resolved href
            String linkUrl = linkElement.getAttribute("href");
            if (isEmpty(linkUrl)) {
                linkUrl = linkElement.getAttribute("src");
                linkElement.removeAttribute("src");
            }

            if (isEmpty(linkUrl)) {
                // Resolve a dynamic component link
                linkUrl = linkResolver.resolveLink(linkElement.getAttributeNS(XLINK_NS_URI, "href"), null, true);
            }

            if (!isEmpty(linkUrl)) {
                linkElement.setAttribute("href", linkUrl);
                applyHashIfApplicable(linkElement);
                removeUnusedAttributes(linkElement);
            } else {
                moveChildrenToParentAndRemoveNode(linkElement);
            }
        }
    }

    private void applyHashIfApplicable(Element linkElement) {
        final String target = linkElement.getAttribute("target");
        if ("anchored".equals(target)) {
            final String href = linkElement.getAttribute("href");
            final String fullRequestPath = webRequestContext.getContextPath()
                    + webRequestContext.getRequestPath();

            final String linkName = getLinkName(linkElement);
            final String hash = !isEmpty(linkName) ? ("#" + linkName.replaceAll(" ", "_").toLowerCase()) : EMPTY;

            if (fullRequestPath.equalsIgnoreCase(href)) {
                linkElement.setAttribute("href", hash);
                linkElement.setAttribute("target", "");
            } else {
                linkElement.setAttribute("href", href + hash);
                linkElement.setAttribute("target", "_top");
            }
        }
    }

    /**
     * Allows to retrieve the inner xml of the first node &lt;xhtml&gt;&lt;/xhtml&gt; as String.
     */
    private String innerXml(Node node) {
        DOMImplementationLS lsImpl = (DOMImplementationLS) node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer lsSerializer = lsImpl.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("xml-declaration", false);
        NodeList childNodes = node.getChildNodes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < childNodes.getLength(); i++) {
            sb.append(lsSerializer.writeToString(childNodes.item(i)));
        }
        return sb.toString();
    }

    private String getLinkName(Element linkElement) {
        final String componentUri = linkElement.getAttributeNS(XLINK_NS_URI, "href");

        try {
            // NOTE: This DD4T method requires a template URI but it does not actually use it; pass a dummy value
            final ComponentPresentation componentPresentation = componentFactory
                    .getComponentPresentation(componentUri, "tcm:0-0-0");

            final String title = (componentPresentation != null) ? componentPresentation.getComponent().getTitle() : EMPTY;
            return isEmpty(title) ? linkElement.getAttribute("title") : title;
        } catch (FactoryException e) {
            return linkElement.getAttribute("title");
        }
    }

    private void removeUnusedAttributes(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            Attr attribute = (Attr) attributes.item(i);
            if (isUnusedAttribute(attribute)) {
                element.removeAttributeNode(attribute);
                i--;
                length--;
            }
        }
    }

    private boolean isUnusedAttribute(Attr attribute) {
        return attribute != null && (StringUtils.startsWithAny(attribute.getLocalName(), "data-", "xlink")
                || StringUtils.startsWithAny(attribute.getName(), "xlink:", "xmlns:"));
    }

    private void moveChildrenToParentAndRemoveNode(Node node) {
        // First get all the children into a new list
        final List<Node> childNodes = new ArrayList<>(new NodeListAdapter(node.getChildNodes()));

        // Then move the ones that are not attributes to the parent one by one
        final Node parentNode = node.getParentNode();
        for (Node childNode : childNodes) {
            if (childNode.getNodeType() != Node.ATTRIBUTE_NODE) {
                parentNode.insertBefore(childNode, node);
            }
        }

        // And finally, remove the node itself from the parent
        parentNode.removeChild(node);
    }

}

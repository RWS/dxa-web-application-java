package com.sdl.webapp.tridion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.sdl.webapp.common.api.model.*;
import com.sdl.webapp.common.exceptions.DxaException;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableBiMap;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.RichTextProcessor;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.util.NamedNodeMapAdapter;
import com.sdl.webapp.common.util.NodeListAdapter;
import com.sdl.webapp.common.util.SimpleNamespaceContext;
import com.sdl.webapp.common.util.XMLUtils;

@Component
public class DefaultRichTextProcessor implements RichTextProcessor {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultRichTextProcessor.class);

    private static final String XHTML_NS_URI = "http://www.w3.org/1999/xhtml";
    private static final String XLINK_NS_URI = "http://www.w3.org/1999/xlink";

    private static final NamespaceContext NAMESPACE_CONTEXT = new SimpleNamespaceContext(
            ImmutableBiMap.<String, String> builder()
                    .put("xhtml", XHTML_NS_URI).put("xlink", XLINK_NS_URI)
                    .build());

    // XPathExpression is not thread-safe
    private static final ThreadLocal<XPathExpression> XPATH_LINKS = new ThreadLocal<XPathExpression>() {
        @Override
        protected XPathExpression initialValue() {
            try {
                final XPath xpath = XPathFactory.newInstance().newXPath();
                xpath.setNamespaceContext(NAMESPACE_CONTEXT);
                return xpath
                        .compile("//a[@xlink:href[starts-with(string(.),'tcm:')]][@href='' or not(@href)]");
            } catch (XPathExpressionException e) {
                throw new RuntimeException(
                        "Error while creating XPath expression", e);
            }
        }
    };

    private static final ThreadLocal<XPathExpression> XPATH_YOUTUBE_VIDEOS = new ThreadLocal<XPathExpression>() {
        @Override
        protected XPathExpression initialValue() {
            try {
                final XPath xpath = XPathFactory.newInstance().newXPath();
                xpath.setNamespaceContext(NAMESPACE_CONTEXT);
                return xpath.compile("//img[@data-youTubeId]");
            } catch (XPathExpressionException e) {
                throw new RuntimeException(
                        "Error while creating XPath expression", e);
            }
        }
    };
    private static final ThreadLocal<XPathExpression> XPATH_IMAGES = new ThreadLocal<XPathExpression>() {
        @Override
        protected XPathExpression initialValue() {
            try {
                final XPath xpath = XPathFactory.newInstance().newXPath();
                xpath.setNamespaceContext(NAMESPACE_CONTEXT);
                return xpath.compile("//img[@data-schemaUri]");
            } catch (XPathExpressionException e) {
                throw new RuntimeException(
                        "Error while creating XPath expression", e);
            }
        }
    };
    @Autowired
    public DefaultRichTextProcessor(MediaHelper mediaHelper, WebRequestContext webRequestContext,
                                    TridionLinkResolver linkResolver, ComponentPresentationFactory componentFactory, ViewModelRegistry viewModelRegistry) {
        this.mediaHelper = mediaHelper;
        this.webRequestContext = webRequestContext;
        this.linkResolver = linkResolver;
        this.componentFactory = componentFactory;
        this.viewModelRegistry = viewModelRegistry;
    }

    private final MediaHelper mediaHelper;

    private final WebRequestContext webRequestContext;

    private final TridionLinkResolver linkResolver;

    private final ViewModelRegistry viewModelRegistry;

    private final ComponentPresentationFactory componentFactory;

    private final String EmbeddedEntityProcessingInstructionName = "EmbeddedEntity";
    @Override
    public RichText processRichText(String xhtml, Localization localization) {
        try {
            // Parse the document as XML
            final Document document = XMLUtils.parse("<xhtml>" + xhtml
                    + "</xhtml>");

            // Resolve links and YouTube videos
            return ResolveRichText(document, localization);

//			// Write the modified document out as XML
//			// Remove XML header and surrounding XHTML start and end tags
//			return cleanHtml(XMLUtils.format(document)
//					.replaceAll("\\A(<\\?xml.*\\?>)?\\s*<xhtml>", "")
//					.replaceAll("</xhtml>\\Z", ""));
        } catch (SAXException | IOException e) {
            LOG.warn("Exception while parsing or processing XML content", e);
            return new RichText(xhtml);
        } catch (ContentProviderException e) {
            LOG.warn("Exception while parsing or processing XML content", e);
            return new RichText(xhtml);
        } catch (SemanticMappingException e) {
            LOG.warn("Exception while parsing or processing XML content", e);
            return new RichText(xhtml);
        }
    }

    private <T extends AbstractEntityModel> T createInstance(Class<? extends T> entityClass) throws SemanticMappingException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("entityClass: {}", entityClass.getName());
        }
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SemanticMappingException("Exception while creating instance of entity class: " +
                    entityClass.getName(), e);
        }
    }

    private RichText ResolveRichText(Document doc, Localization localization) throws ContentProviderException, SemanticMappingException
    {
        List<RichTextFragment> richTextFragments = new LinkedList<RichTextFragment>();
        this.resolveLinks(doc);

        List<EntityModel> embeddedEntities = new LinkedList<EntityModel>();

        List<Node> entityElements = null;
        try {
            entityElements = new ArrayList<>(
                    new NodeListAdapter((NodeList) XPATH_IMAGES.get().evaluate(
                            doc, XPathConstants.NODESET)));
        } catch (XPathExpressionException e) {
            LOG.warn("Error while evaluation XPath expression", e);
        }

        if(entityElements != null) {
            for (Node imgElement : entityElements) {
                String[] schemaTcmUriParts = imgElement.getAttributes().getNamedItem("data-schemaUri").getNodeValue().split("-");
                final SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(schemaTcmUriParts[1]));

                String viewName = semanticSchema.getRootElement();
                final Class<? extends AbstractEntityModel> entityClass;
                try {
                    entityClass = (Class<? extends AbstractEntityModel>) viewModelRegistry.getMappedModelTypes(viewName);
                    if (entityClass == null) {
                        throw new ContentProviderException("Cannot determine entity type for view name: '" + viewName +
                                "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
                    }
                } catch (DxaException e) {
                    throw new ContentProviderException("Cannot determine entity type for view name: '" + viewName +
                            "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.", e);
                }

                MediaItem mediaItem = (MediaItem) createInstance(entityClass);
                mediaItem.readFromXhtmlElement(imgElement);
                embeddedEntities.add(mediaItem);

                imgElement.getParentNode().replaceChild(doc.createProcessingInstruction(EmbeddedEntityProcessingInstructionName, ""), imgElement);
            }
        }

        String xhtml;
        try {
            XMLUtils.format(doc);
            xhtml = innerXml(doc.getFirstChild());


            int lastFragmentIndex = 0;
            int i = 0;


            Pattern pattern = Pattern.compile("<\\?EmbeddedEntity\\s?\\?>");
            Matcher matcher = pattern.matcher(xhtml);

            while (matcher.find()) {
                String match = matcher.group();

                int embeddedEntityIndex = matcher.start();

                if (embeddedEntityIndex > lastFragmentIndex)
                {
                    richTextFragments.add(new RichTextFragmentImpl(xhtml.substring(lastFragmentIndex, embeddedEntityIndex)));
                }
                richTextFragments.add((RichTextFragment)embeddedEntities.get(i++));
                lastFragmentIndex = matcher.end();
            }

            if (lastFragmentIndex < xhtml.length())
            {
                // Final text fragment
                richTextFragments.add(new RichTextFragmentImpl(xhtml.substring(lastFragmentIndex)));
            }

        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new RichText(richTextFragments);
    }

    /**
     * Allows to retrieve the inner xml of the first node <xhtml></xhtml> as String
     * @param node
     * @return
     */
    private String innerXml(Node node) {
        DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer lsSerializer = lsImpl.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("xml-declaration", false);
        NodeList childNodes = node.getChildNodes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < childNodes.getLength(); i++) {
            sb.append(lsSerializer.writeToString(childNodes.item(i)));
        }
        return sb.toString();
    }
    /*
    private String cleanHtml(String input) {
        // This is necessary to convert the XHTML to valid HTML that the browser
        // understands;
        // note that the pages are HTML5, and including the XHTML in an HTML5
        // page as-is leads to
        // strange rendering problems in some browsers.
        final HtmlCleaner cleaner = new HtmlCleaner();
        final TagNode node = cleaner.clean(input);
        return cleaner.getInnerHtml(node);
    }

    */
    private void resolveLinks(Document document) {
        try {
            // NOTE: Put link elements in a new list to avoid problems while
            // editing
            final List<Node> linkElements = new ArrayList<>(
                    new NodeListAdapter((NodeList) XPATH_LINKS.get().evaluate(
                            document, XPathConstants.NODESET)));

            for (Node linkNode : linkElements) {
                final Element linkElement = (Element) linkNode;

                // Check if this link already has a resolved href
                String linkUrl = linkElement.getAttribute("href");
                if (Strings.isNullOrEmpty(linkUrl)) {
                    // Check if there is a src attribute; if there is, remove it
                    linkUrl = linkElement.getAttribute("src");
                    linkElement.removeAttribute("src");
                }

                if (Strings.isNullOrEmpty(linkUrl)) {
                    // Resolve a dynamic component link
                    linkUrl = linkResolver.resolveLink(
                            linkElement.getAttributeNS(XLINK_NS_URI, "href"),
                            null, true);
                }

                if (!Strings.isNullOrEmpty(linkUrl)) {
                    // Add href attribute
                    linkElement.setAttribute("href", linkUrl);

                    applyHashIfApplicable(linkElement);

                    // Remove all xlink attributes
                    removeXlinkAttributes(linkElement);
                } else {
                    // Move the child elements to the parent and remove the link
                    // element itself
                    moveChildrenToParentAndRemoveNode(linkElement);
                }
            }
        } catch (XPathExpressionException e) {
            LOG.warn("Error while evaluation XPath expression", e);
        }
    }

    private void applyHashIfApplicable(Element linkElement) {
        final String target = linkElement.getAttribute("target");
        if ("anchored".equals(target)) {
            final String href = linkElement.getAttribute("href");
            final String fullRequestPath = webRequestContext.getContextPath()
                    + webRequestContext.getRequestPath();

            final String linkName = getLinkName(linkElement);
            final String hash = !Strings.isNullOrEmpty(linkName) ? "#"
                    + linkName.replaceAll(" ", "_").toLowerCase() : "";

            if (fullRequestPath.equalsIgnoreCase(href)) {
                linkElement.setAttribute("href", hash);
                linkElement.setAttribute("target", "");
            } else {
                linkElement.setAttribute("href", href + hash);
                linkElement.setAttribute("target", "_top");
            }
        }
    }

    private String getLinkName(Element linkElement) {
        final String componentUri = linkElement.getAttributeNS(XLINK_NS_URI,
                "href");

        try {
            // NOTE: This DD4T method requires a template URI but it does not
            // actually use it; pass a dummy value
            final ComponentPresentation componentPresentation = componentFactory
                    .getComponentPresentation(componentUri, "tcm:0-0-0");

            final String title = componentPresentation != null ? componentPresentation
                    .getComponent().getTitle() : "";
            return !Strings.isNullOrEmpty(title) ? title : linkElement
                    .getAttribute("title");
        } catch (FactoryException e) {
            return linkElement.getAttribute("title");
        }
    }

    private void removeXlinkAttributes(Element element) {
        // NOTE: The org.w3c.dom API unfortunately has many design and
        // implementation flaws, which makes it very
        // user-unfriendly and hard to work with.
        boolean done;
        do {
            done = true;
            for (Node attrNode : new NamedNodeMapAdapter(
                    element.getAttributes())) {
                if (XLINK_NS_URI.equals(attrNode.getNamespaceURI())
                        || (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attrNode
                        .getNamespaceURI()) && attrNode.getLocalName()
                        .equals("xlink"))) {
                    element.removeAttributeNode((Attr) attrNode);
                    done = false;
                    break;
                }
            }
        } while (!done);
    }

    private void moveChildrenToParentAndRemoveNode(Node node) {
        // First get all the children into a new list
        final List<Node> childNodes = new ArrayList<>(new NodeListAdapter(
                node.getChildNodes()));

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

package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableBiMap;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentResolver;
import com.sdl.webapp.common.util.NamedNodeMapAdapter;
import com.sdl.webapp.common.util.NodeListAdapter;
import com.sdl.webapp.common.util.SimpleNamespaceContext;
import com.sdl.webapp.common.util.XMLUtils;
import com.sdl.webapp.tridion.TridionLinkResolver;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.dd4t.DD4TContentProvider.DEFAULT_PAGE_EXTENSION;
import static com.sdl.webapp.dd4t.DD4TContentProvider.DEFAULT_PAGE_NAME;

@Component
public class DD4TContentResolver implements ContentResolver {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentResolver.class);

    private static final String XHTML_NS_URI = "http://www.w3.org/1999/xhtml";
    private static final String XLINK_NS_URI = "http://www.w3.org/1999/xlink";

    private static final NamespaceContext NAMESPACE_CONTEXT = new SimpleNamespaceContext(
            ImmutableBiMap.<String, String>builder()
                    .put("xhtml", XHTML_NS_URI)
                    .put("xlink", XLINK_NS_URI)
                    .build());

    // XPathExpression is not thread-safe
    private static final ThreadLocal<XPathExpression> XPATH_LINKS = new ThreadLocal<XPathExpression>() {
        @Override
        protected XPathExpression initialValue() {
            try {
                final XPath xpath = XPathFactory.newInstance().newXPath();
                xpath.setNamespaceContext(NAMESPACE_CONTEXT);
                return xpath.compile("//a[@xlink:href[starts-with(string(.),'tcm:')]][@href='' or not(@href)]");
            } catch (XPathExpressionException e) {
                throw new RuntimeException("Error while creating XPath expression", e);
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
                throw new RuntimeException("Error while creating XPath expression", e);
            }
        }
    };

    private final MediaHelper mediaHelper;

    private final WebRequestContext webRequestContext;

    private final TridionLinkResolver linkResolver;

    private final ComponentPresentationFactory componentFactory;

    @Autowired
    public DD4TContentResolver(MediaHelper mediaHelper, WebRequestContext webRequestContext,
                               TridionLinkResolver linkResolver, ComponentPresentationFactory componentFactory) {
        this.mediaHelper = mediaHelper;
        this.webRequestContext = webRequestContext;
        this.linkResolver = linkResolver;
        this.componentFactory = componentFactory;
    }

    @Override
    public String resolveLink(String url, String localizationId) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;
        String resolvedUrl = linkResolver.resolveLink(url, publicationId, false);

        if (!Strings.isNullOrEmpty(resolvedUrl)) {
            if (resolvedUrl.endsWith(DEFAULT_PAGE_EXTENSION)) {
                resolvedUrl = resolvedUrl.substring(0, resolvedUrl.length() - DEFAULT_PAGE_EXTENSION.length());
            }
            if (resolvedUrl.endsWith("/" + DEFAULT_PAGE_NAME)) {
                resolvedUrl = resolvedUrl.substring(0, resolvedUrl.length() - DEFAULT_PAGE_NAME.length());
            }
        }

        return resolvedUrl;
    }

    @Override
    public String resolveContent(String content) {
        try {
            // Parse the document as XML
            final Document document = XMLUtils.parse("<xhtml>" + content + "</xhtml>");

            // Resolve links and YouTube videos
            resolveLinks(document);
            resolveYouTubeVideos(document);

            // Write the modified document out as XML
            // Remove XML header and surrounding XHTML start and end tags
            return cleanHtml(XMLUtils.format(document)
                    .replaceAll("\\A(<\\?xml.*\\?>)?\\s*<xhtml>", "").replaceAll("</xhtml>\\Z", ""));
        } catch (SAXException | IOException | TransformerException e) {
            LOG.warn("Exception while parsing or processing XML content", e);
            return content;
        }
    }

    private String cleanHtml(String input) {
        // This is necessary to convert the XHTML to valid HTML that the browser understands;
        // note that the pages are HTML5, and including the XHTML in an HTML5 page as-is leads to
        // strange rendering problems in some browsers.
        final HtmlCleaner cleaner = new HtmlCleaner();
        final TagNode node = cleaner.clean(input);
        return cleaner.getInnerHtml(node);
    }

    private void resolveLinks(Document document) {
        try {
            // NOTE: Put link elements in a new list to avoid problems while editing
            final List<Node> linkElements = new ArrayList<>(new NodeListAdapter((NodeList) XPATH_LINKS.get()
                    .evaluate(document, XPathConstants.NODESET)));

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
                    linkUrl = resolveLink(linkElement.getAttributeNS(XLINK_NS_URI, "href"), null);
                }

                if (!Strings.isNullOrEmpty(linkUrl)) {
                    // Add href attribute
                    linkElement.setAttribute("href", linkUrl);

                    applyHashIfApplicable(linkElement);

                    // Remove all xlink attributes
                    removeXlinkAttributes(linkElement);
                } else {
                    // Move the child elements to the parent and remove the link element itself
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
            final String href= linkElement.getAttribute("href");
            final String fullRequestPath = webRequestContext.getContextPath() + webRequestContext.getRequestPath();

            final String linkName = getLinkName(linkElement);
            final String hash = !Strings.isNullOrEmpty(linkName) ? "#" + linkName.replaceAll(" ", "_").toLowerCase() : "";

            if (fullRequestPath.equalsIgnoreCase(fullRequestPath)) {
                linkElement.setAttribute("href", hash);
                linkElement.setAttribute("target", "");
            } else {
                linkElement.setAttribute("href", href + hash);
                linkElement.setAttribute("target", "_top");
            }
        }
    }

    private String getLinkName(Element linkElement) {
        final String componentUri = linkElement.getAttributeNS(XLINK_NS_URI, "href");

        try {
            // NOTE: This DD4T method requires a template URI but it does not actually use it; pass a dummy value
            final ComponentPresentation componentPresentation = componentFactory.getComponentPresentation(componentUri, "tcm:0-0-0");

            final String title = componentPresentation != null ? componentPresentation.getComponent().getTitle() : "";
            return !Strings.isNullOrEmpty(title) ? title : linkElement.getAttribute("title");
        } catch (FactoryException e) {
            return linkElement.getAttribute("title");
        }
    }

    private void removeXlinkAttributes(Element element) {
        // NOTE: The org.w3c.dom API unfortunately has many design and implementation flaws, which makes it very
        // user-unfriendly and hard to work with.
        boolean done;
        do {
            done = true;
            for (Node attrNode : new NamedNodeMapAdapter(element.getAttributes())) {
                if (XLINK_NS_URI.equals(attrNode.getNamespaceURI()) ||
                        (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attrNode.getNamespaceURI()) &&
                                attrNode.getLocalName().equals("xlink"))) {
                    element.removeAttributeNode((Attr) attrNode);
                    done = false;
                    break;
                }
            }
        } while (!done);
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

    private void resolveYouTubeVideos(Document document) {
        try {
            // NOTE: Put link elements in a new list to avoid problems while editing
            final List<Node> youTubeElements = new ArrayList<>(new NodeListAdapter((NodeList) XPATH_YOUTUBE_VIDEOS.get()
                    .evaluate(document, XPathConstants.NODESET)));

            for (Node youTubeNode : youTubeElements) {
                final Element youTubeElement = (Element) youTubeNode;

                final String url = youTubeElement.getAttributeNS(XLINK_NS_URI, "href");
                if (!Strings.isNullOrEmpty(url)) {
                    final String youTubeId = youTubeElement.getAttribute("data-youTubeId");
                    final String headline = youTubeElement.getAttribute("data-headline");
                    final String src = youTubeElement.getAttribute("src");

                    final String placeholderImageUrl = mediaHelper.getResponsiveImageUrl(src, "100%", 0.0, 0);

                    final Element span = document.createElement("span");
                    span.setAttribute("class", "embed-video");

                    final Element img = document.createElement("img");
                    img.setAttribute("src", webRequestContext.getContextPath() + placeholderImageUrl);
                    img.setAttribute("alt", headline);
                    span.appendChild(img);

                    final Element button = document.createElement("button");
                    button.setAttribute("data-video", youTubeId);
                    final Element playButtonOverlay = document.createElement("i");
                    playButtonOverlay.setAttribute("class", "fa fa-play-circle");
                    button.appendChild(playButtonOverlay);
                    span.appendChild(button);

                    youTubeElement.getParentNode().replaceChild(span, youTubeElement);
                }
            }
        } catch (XPathExpressionException e) {
            LOG.warn("Error while evaluation XPath expression", e);
        }
    }
}

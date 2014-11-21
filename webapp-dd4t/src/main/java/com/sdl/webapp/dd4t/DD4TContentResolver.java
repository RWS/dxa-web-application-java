package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableBiMap;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.util.NamedNodeMapAdapter;
import com.sdl.webapp.common.api.content.ContentResolver;
import com.sdl.webapp.common.util.NodeListAdapter;
import com.sdl.webapp.common.util.SimpleNamespaceContext;
import com.sdl.webapp.common.util.XMLUtils;
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
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    public DD4TContentResolver(MediaHelper mediaHelper, WebRequestContext webRequestContext) {
        this.mediaHelper = mediaHelper;
        this.webRequestContext = webRequestContext;
    }

    @Override
    public String resolveLink(String url) {
        // TODO: TSI-521 Implement this method
        return url;
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
            return XMLUtils.format(document)
                    .replaceAll("\\A(<\\?xml.*\\?>)?\\s*<xhtml>", "").replaceAll("</xhtml>\\Z", "");
        } catch (SAXException | IOException | TransformerException e) {
            LOG.warn("Exception while parsing or processing XML content", e);
            return content;
        }
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
                    linkUrl = resolveLink(linkElement.getAttributeNS(XLINK_NS_URI, "href"));
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
        // TODO: Implement this method
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

                    final Element div = document.createElement("div");
                    div.setAttribute("class", "embed-video");

                    final Element img = document.createElement("img");
                    img.setAttribute("src", webRequestContext.getContextPath() + placeholderImageUrl);
                    img.setAttribute("alt", headline);
                    div.appendChild(img);

                    final Element button = document.createElement("button");
                    button.setAttribute("data-video", youTubeId);
                    final Element playButtonOverlay = document.createElement("i");
                    playButtonOverlay.setAttribute("class", "fa fa-play-circle");
                    button.appendChild(playButtonOverlay);
                    div.appendChild(button);

                    document.replaceChild(div, youTubeElement);
                }
            }
        } catch (XPathExpressionException e) {
            LOG.warn("Error while evaluation XPath expression", e);
        }
    }
}

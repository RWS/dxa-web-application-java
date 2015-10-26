package com.sdl.webapp.tridion.xpath;

import com.google.common.collect.ImmutableBiMap;
import com.sdl.webapp.common.util.SimpleNamespaceContext;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Resolver for particular xPath string.
 * Uses {@link XPathExpression} which is not thread-safe.
 */
public enum XPathResolver {
    XPATH_LINKS("//a[@xlink:href]"),
    XPATH_YOUTUBE("//img[@data-youTubeId]"),
    XPATH_IMAGES("//img[@data-schemaUri]");


    public static final String XHTML_NS_URI = "http://www.w3.org/1999/xhtml";
    public static final String XLINK_NS_URI = "http://www.w3.org/1999/xlink";

    private static final NamespaceContext NAMESPACE_CONTEXT = new SimpleNamespaceContext(
            ImmutableBiMap.<String, String>builder()
                    .put("xhtml", XHTML_NS_URI)
                    .put("xlink", XLINK_NS_URI)
                    .build());

    private ThreadLocal<XPathExpression> expression;
    private String sourceString;

    XPathResolver(final String sourceString) {
        // note that PathExpression is not thread-safe
        this(sourceString, new ThreadLocal<XPathExpression>() {
            @Override
            protected XPathExpression initialValue() {
                try {
                    final XPath xpath = XPathFactory.newInstance().newXPath();
                    xpath.setNamespaceContext(NAMESPACE_CONTEXT);
                    return xpath.compile(sourceString);
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(
                            "Error while creating XPath expression", e);
                }
            }
        });
    }

    XPathResolver(String sourceString, ThreadLocal<XPathExpression> expression) {
        this.expression = expression;
        this.sourceString = sourceString;
    }

    public ThreadLocal<XPathExpression> getExpression() {
        return expression;
    }

    public ThreadLocal<XPathExpression> expr() {
        return getExpression();
    }

    public String getSourceString() {
        return sourceString;
    }
}

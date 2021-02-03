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
 * Uses {@link javax.xml.xpath.XPathExpression} which is not thread-safe.
 */
@Deprecated
public enum XPathResolver {
    XPATH_LINKS("//a[@xlink:href]"),
    XPATH_YOUTUBE("//img[@data-youTubeId]"),
    XPATH_IMAGES("//img[@data-schemaUri]");


    /**
     * Constant <code>XHTML_NS_URI="http://www.w3.org/1999/xhtml"</code>
     */
    public static final String XHTML_NS_URI = "http://www.w3.org/1999/xhtml";
    /**
     * Constant <code>XLINK_NS_URI="http://www.w3.org/1999/xlink"</code>
     */
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
                    /**
                     * This hardcoded XPathFactory is needed to avoid the possibility that a wrong implementation is
                     * being loaded from classpath when using the javax.xml.xpath.XPathFactory. (cfr javadoc newInstance())
                     *
                     * Example of a wrong implementation: org.apache.taglibs.standard.tag.common.xml.JSTLXPathFactory.
                     * Issue: XPathExpression will be null for xpath.compile!
                     */
                    XPathFactory xPathFactory = new org.apache.xpath.jaxp.XPathFactoryImpl();
                    final XPath xpath = xPathFactory.newXPath();
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

    /**
     * <p>Getter for the field <code>expression</code>.</p>
     *
     * @return a {@link java.lang.ThreadLocal} object.
     */
    public ThreadLocal<XPathExpression> getExpression() {
        return expression;
    }

    /**
     * <p>expr.</p>
     *
     * @return a {@link java.lang.ThreadLocal} object.
     */
    public ThreadLocal<XPathExpression> expr() {
        return expression;
    }

    /**
     * <p>Getter for the field <code>sourceString</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSourceString() {
        return sourceString;
    }
}

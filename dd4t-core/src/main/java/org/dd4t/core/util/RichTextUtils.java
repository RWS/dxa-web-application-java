package org.dd4t.core.util;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.resolvers.LinkResolver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public class RichTextUtils {

    private static final Pattern TCM = Pattern.compile("^tcm:");
    private static final String XLINK_HREF = "xlink:href";
    private static final String XHTMLBODYROOT = "xhtmlbodyroot";
    private static final String IMG_TAG = "img";
    private static final String SRC_ATTR = "src";

    private RichTextUtils () {

    }

    public static String resolveXhtmlBody(final String body, boolean resolveLinks, final LinkResolver linkResolver, final String contextPath) throws ItemNotFoundException, SerializationException {
        return doReplacement(resolveXhtml(resolveLinks, linkResolver, contextPath, body));
    }

    // TODO: add viewModel resolving for AFTER_CACHING processing.

    /**
     * This method takes care of:
     * 1. Formatting RTF from Tridion. This basically means
     * Stripping out the xhtml and xlink namespaces
     * 2. Resolve any component links which can be
     *
     * @param xhtmlFields  the DD4T Xhtml Field
     * @param resolveLinks resolve links as well as stripping namespaces
     * @param linkResolver a concrete link resolver
     */
    public static void resolveXhtmlField (final XhtmlField xhtmlFields, final boolean resolveLinks, final LinkResolver linkResolver, final String contextPath) throws ItemNotFoundException, SerializationException {

        List<Object> xhtmlValues = xhtmlFields.getValues();
        List<String> newValues = new ArrayList<>();
        String contextPathToUse = contextPath == null ? "" : contextPath;

        for (Object xhtmlField : xhtmlValues) {

            if (StringUtils.isEmpty((String) xhtmlField)) {
                newValues.add("");
            } else {
                Element xhtmlBodyRoot = resolveXhtml(resolveLinks, linkResolver, contextPathToUse, (String)xhtmlField);

                if (xhtmlBodyRoot != null) {
                    newValues.add(doReplacement(xhtmlBodyRoot));
                }
            }
        }

        xhtmlFields.setTextValues(newValues);
    }

    protected static String doReplacement(final Element xhtmlBodyRoot) {
        return xhtmlBodyRoot.html().replaceAll("(?ims)xlink:|xmlns(=\"http://www\\.w3\\.org/1999/xhtml\"\\s*|:xlink=\"http://www\\.w3\\.org/1999/xlink\"\\s*)", "");
    }

    protected static Element resolveXhtml(final boolean resolveLinks, final LinkResolver linkResolver, final String
            contextPathToUse, final String xhtmlField) throws SerializationException, ItemNotFoundException {
        Document document = Jsoup.parseBodyFragment("<" + XHTMLBODYROOT + ">" + xhtmlField + "</" + XHTMLBODYROOT + ">");
        document.outputSettings().indentAmount(0).prettyPrint(false);

        Element xhtmlBodyRoot = document.getElementsByTag(XHTMLBODYROOT).first();
        if (resolveLinks && linkResolver != null) {
            Elements links = xhtmlBodyRoot.getElementsByAttributeValueMatching(XLINK_HREF, TCM);
            for (Element link : links) {
                String resolvedLink = linkResolver.resolve(link.attr(XLINK_HREF));
                if (StringUtils.isNotEmpty(resolvedLink)) {
                    link.attr(XLINK_HREF, contextPathToUse + resolvedLink);
                } else {
                    link.attr(XLINK_HREF, "");
                }
            }
        }

        // Add the context path to img tags

        if (StringUtils.isNotEmpty(contextPathToUse)) {
            Elements images = xhtmlBodyRoot.getElementsByTag(IMG_TAG);

            for (Element image : images) {
                String src = image.attr(SRC_ATTR);
                image.attr(SRC_ATTR, contextPathToUse + src);
            }
        }
        return xhtmlBodyRoot;
    }
}

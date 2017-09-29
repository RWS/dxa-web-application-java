package com.sdl.webapp.tridion.linking;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.LinkResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Abstract AbstractTridionLinkResolver class.</p>
 */
public abstract class AbstractTridionLinkResolver implements LinkResolver {
    //TODO : move these back to defaultcontentprovider once class is moved to new package
    /**
     * Constant <code>DEFAULT_PAGE_NAME="index"</code>
     */
    public static final String DEFAULT_PAGE_NAME = "index";
    /**
     * Constant <code>DEFAULT_PAGE_EXTENSION=".html"</code>
     */
    public static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTridionLinkResolver.class);

    /** {@inheritDoc} */
    @Override
    public String resolveLink(String url, String localizationId) {
        return resolveLink(url, localizationId, false);
    }

    /** {@inheritDoc} */
    @Override
    public String resolveLink(String url, String localizationId, boolean resolveToBinary) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;
        String resolvedUrl = resolveLink(url, publicationId, resolveToBinary);

        if (url.startsWith("tcm:")) {
            if (!StringUtils.isEmpty(resolvedUrl)) {
                if (resolvedUrl.endsWith(DEFAULT_PAGE_EXTENSION)) {
                    resolvedUrl = resolvedUrl.substring(0, resolvedUrl.length() - DEFAULT_PAGE_EXTENSION.length());
                }
                if (resolvedUrl.endsWith('/' + DEFAULT_PAGE_NAME)) {
                    resolvedUrl = resolvedUrl.substring(0, resolvedUrl.length() - DEFAULT_PAGE_NAME.length());
                }
            }
        }
        return resolvedUrl;
    }

    /** {@inheritDoc} */
    public String resolveLink(String uri, int publicationId, boolean isBinary) {
        if (uri == null || !uri.startsWith("tcm:")) {
            return uri;
        }

        // Remove the "tcm:" prefix
        uri = uri.substring(4);

        final String[] parts = uri.split("-");

        if (publicationId == 0) {
            publicationId = Integer.parseInt(parts[0]);
        }

        final int itemId = Integer.parseInt(parts[1]);
        final int itemType = parts.length > 2 && !parts[2].startsWith("v") ? Integer.parseInt(parts[2]) : 16;

        switch (itemType) {
            case 16:
                if (isBinary) {
                    String resolvedLink = resolveLink(BasicLinkStrategy.BINARY_LINK_STRATEGY, publicationId, uri);

                    if (!StringUtils.isEmpty(resolvedLink)) {
                        return resolvedLink;
                    }
                }

                return resolveLink(BasicLinkStrategy.COMPONENT_LINK_STRATEGY, publicationId, itemId);
            case 64:
                return resolveLink(BasicLinkStrategy.PAGE_LINK_STRATEGY, publicationId, itemId);

            default:
                LOG.warn("Could not resolve link: {}", uri);
                return "";
        }
    }

    /**
     * <p>resolveLink.</p>
     *
     * @param linkStrategy a {@link com.sdl.webapp.tridion.linking.AbstractTridionLinkResolver.BasicLinkStrategy} object.
     * @param publicationId a int.
     * @param itemId a int.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected abstract String resolveLink(BasicLinkStrategy linkStrategy, int publicationId, int itemId, String uri);

    private String resolveLink(BasicLinkStrategy linkStrategy, int publicationId, int itemId) {
        return resolveLink(linkStrategy, publicationId, itemId, null);
    }

    private String resolveLink(BasicLinkStrategy linkStrategy, int publicationId, String uri) {
        return resolveLink(linkStrategy, publicationId, 0, uri);
    }

    protected enum BasicLinkStrategy {
        BINARY_LINK_STRATEGY,
        COMPONENT_LINK_STRATEGY,
        PAGE_LINK_STRATEGY
    }
}

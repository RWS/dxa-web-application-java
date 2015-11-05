package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.tridion.linking.BinaryLink;
import com.tridion.linking.ComponentLink;
import com.tridion.linking.Link;
import com.tridion.linking.PageLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TridionLinkResolver implements LinkResolver {
    //TODO : move these back to defaultcontentprovider once class is moved to new package
    public static final String DEFAULT_PAGE_NAME = "index";
    public static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static final Logger LOG = LoggerFactory.getLogger(TridionLinkResolver.class);

    private static final LinkStrategy BINARY_LINK_STRATEGY = new LinkStrategy() {
        @Override
        public Link getLink(int publicationId, int itemId, String uri) {
            return new BinaryLink(publicationId)
                    .getLink(uri.startsWith("tcm:") ? uri : ("tcm:" + uri), null, null, null, false);
        }
    };
    private static final LinkStrategy COMPONENT_LINK_STRATEGY = new LinkStrategy() {
        @Override
        public Link getLink(int publicationId, int itemId, String uri) {
            return new ComponentLink(publicationId).getLink(itemId);
        }
    };
    private static final LinkStrategy PAGE_LINK_STRATEGY = new LinkStrategy() {
        @Override
        public Link getLink(int publicationId, int itemId, String uri) {
            return new PageLink(publicationId).getLink(itemId);
        }
    };

    @Override
    public String resolveLink(String url, String localizationId) {
        return resolveLink(url, localizationId, false);
    }

    @Override
    public String resolveLink(String url, String localizationId, boolean resolveToBinary) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;
        String resolvedUrl = resolveLink(url, publicationId, resolveToBinary);

        if (url.startsWith("tcm:")) {
            if (!StringUtils.isEmpty(resolvedUrl)) {
                if (resolvedUrl.endsWith(DEFAULT_PAGE_EXTENSION)) {
                    resolvedUrl = resolvedUrl.substring(0, resolvedUrl.length() - DEFAULT_PAGE_EXTENSION.length());
                }
                if (resolvedUrl.endsWith("/" + DEFAULT_PAGE_NAME)) {
                    resolvedUrl = resolvedUrl.substring(0, resolvedUrl.length() - DEFAULT_PAGE_NAME.length());
                }
            }
        }
        return resolvedUrl;
    }

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
        final int itemType = parts.length > 2 ? Integer.parseInt(parts[2]) : 16;

        switch (itemType) {
            case 16:
                if (isBinary) {
                    String resolvedLink = resolveLink(BINARY_LINK_STRATEGY, publicationId, uri);

                    if (!StringUtils.isEmpty(resolvedLink)) {
                        return resolvedLink;
                    }
                }

                return resolveLink(COMPONENT_LINK_STRATEGY, publicationId, itemId);
            case 64:
                return resolveLink(PAGE_LINK_STRATEGY, publicationId, itemId);

            default:
                LOG.warn("Could not resolve link: {}", uri);
                return "";
        }
    }

    private interface LinkStrategy {
        Link getLink(int publicationId, int itemId, String uri);
    }
    private String resolveLink(LinkStrategy linkStrategy, int publicationId, int itemId) {
        return resolveLink(linkStrategy, publicationId, itemId, null);
    }
    private String resolveLink(LinkStrategy linkStrategy, int publicationId, String uri) {
        return resolveLink(linkStrategy, publicationId, 0, uri);
    }
    private synchronized String resolveLink(LinkStrategy linkStrategy, int publicationId, int itemId, String uri) {
        final Link link = linkStrategy.getLink(publicationId, itemId, uri);
        return link.isResolved() ? link.getURL() : "";
    }
}

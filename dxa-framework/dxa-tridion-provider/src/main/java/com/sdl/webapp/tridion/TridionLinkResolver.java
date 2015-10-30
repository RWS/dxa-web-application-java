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
    private static final Logger LOG = LoggerFactory.getLogger(TridionLinkResolver.class);

    //TODO : move these back to defaultcontentprovider once class is moved to new package
    public static final String DEFAULT_PAGE_NAME = "index";
    public static final String DEFAULT_PAGE_EXTENSION = ".html";


    @Override
    public String resolveLink(String url, String localizationId) {
        return resolveLink(url, localizationId, false);
    }

    @Override
    public String resolveLink(String url, String localizationId, boolean resolveToBinary) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;
        String resolvedUrl = resolveLink(url, publicationId, resolveToBinary);

        if(url.startsWith("tcm:")) {
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
                    String resolvedLink = resolveBinaryLink(uri, publicationId);
                    return resolvedLink.equals("") ? resolveComponentLink(uri, publicationId, itemId) : resolvedLink;
                } else {
                    return resolveComponentLink(uri, publicationId, itemId);
                }
            case 64:
                return resolvePageLink(uri, publicationId, itemId);

            default:
                LOG.warn("Could not resolve link: {}", uri);
                return "";
        }
    }

    private String resolveComponentLink(String uri, int publicationId, int itemId) {
        final Link link = new ComponentLink(publicationId).getLink(itemId);
        return link.isResolved() ? link.getURL() : "";
    }

    private String resolveBinaryLink(String uri, int publicationId) {
        final Link link = new BinaryLink(publicationId).getLink(uri.startsWith("tcm:") ? uri : ("tcm:" + uri),
                null, null, null, false);
        return link.isResolved() ? link.getURL() : "";
    }

    private String resolvePageLink(String uri, int publicationId, int itemId) {
        final Link link = new PageLink(publicationId).getLink(itemId);
        return link.isResolved() ? link.getURL() : "";
    }
}

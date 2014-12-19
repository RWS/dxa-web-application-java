package com.sdl.webapp.tridion;

import com.tridion.linking.BinaryLink;
import com.tridion.linking.ComponentLink;
import com.tridion.linking.Link;
import com.tridion.linking.PageLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TridionLinkResolver {
    private static final Logger LOG = LoggerFactory.getLogger(TridionLinkResolver.class);

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
                return isBinary ? resolveBinaryLink(uri, publicationId) : resolveComponentLink(uri, publicationId, itemId);

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

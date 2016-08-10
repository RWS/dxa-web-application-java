package com.sdl.webapp.tridion.linking;

import com.sdl.web.api.linking.BinaryLinkImpl;
import com.sdl.web.api.linking.ComponentLinkImpl;
import com.sdl.web.api.linking.Link;
import com.sdl.web.api.linking.PageLinkImpl;
import lombok.Synchronized;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TridionLinkResolver extends AbstractTridionLinkResolver {

    private static final LinkStrategy BINARY_LINK_STRATEGY = new LinkStrategy() {
        @Override
        public Link getLink(int publicationId, int itemId, String uri) {
            return new BinaryLinkImpl(publicationId)
                    .getLink(uri.startsWith("tcm:") ? uri : ("tcm:" + uri), null, null, null, false);
        }
    };

    private static final LinkStrategy COMPONENT_LINK_STRATEGY = new LinkStrategy() {
        @Override
        public Link getLink(int publicationId, int itemId, String uri) {
            return new ComponentLinkImpl(publicationId).getLink(itemId);
        }
    };

    private static final LinkStrategy PAGE_LINK_STRATEGY = new LinkStrategy() {
        @Override
        public Link getLink(int publicationId, int itemId, String uri) {
            return new PageLinkImpl(publicationId).getLink(itemId);
        }
    };

    private static Map<BasicLinkStrategy, LinkStrategy> strategiesMapping;

    static {
        strategiesMapping = new HashMap<>();
        strategiesMapping.put(BasicLinkStrategy.BINARY_LINK_STRATEGY, BINARY_LINK_STRATEGY);
        strategiesMapping.put(BasicLinkStrategy.COMPONENT_LINK_STRATEGY, COMPONENT_LINK_STRATEGY);
        strategiesMapping.put(BasicLinkStrategy.PAGE_LINK_STRATEGY, PAGE_LINK_STRATEGY);
    }

    @Override
    protected String resolveLink(BasicLinkStrategy linkStrategy, int publicationId, int itemId, String uri) {
        return resolveLink(strategiesMapping.get(linkStrategy), publicationId, itemId, uri);
    }

    @Synchronized
    private String resolveLink(LinkStrategy linkStrategy, int publicationId, int itemId, String uri) {
        final Link link = linkStrategy.getLink(publicationId, itemId, uri);
        return link.isResolved() ? link.getURL() : "";
    }

    private interface LinkStrategy {

        Link getLink(int publicationId, int itemId, String uri);
    }
}

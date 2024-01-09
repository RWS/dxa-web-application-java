package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.CompositeOutputCacheKeyBase;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Output cache that accepts a composite key as a required key. This is a default implementation for manual access.
 *
 * @see CompositeOutputCacheKeyBase
 */
@Component
public class OutputCache extends SimpleCacheWrapper<CompositeOutputCacheKeyBase, HtmlNode> {

    private static final String USER_AGENT_HEADER = "User-Agent";

    @Override
    public String getCacheName() {
        return "output";
    }

    @Override
    public Class<HtmlNode> getValueType() {
        return HtmlNode.class;
    }

    @Override
    public Object getSpecificKey(CompositeOutputCacheKeyBase keyBase, Object... keyParams) {
        HttpServletRequest request = keyBase.getRequest();
        return getKey(keyBase.getPageId(),
                keyBase.getName(),
                keyBase.getMvcData(),
                keyBase.getInclude(),
                request == null ? "" : request.getHeader(USER_AGENT_HEADER));
    }
}

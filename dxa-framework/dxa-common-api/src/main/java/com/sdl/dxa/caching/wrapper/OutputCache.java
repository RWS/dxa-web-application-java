package com.sdl.dxa.caching.wrapper;

import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Output cache that accepts a composite key as a required key.
 *
 * @see CompositeOutputCacheKey
 */
@Component
public class OutputCache extends SimpleCacheWrapper<CompositeOutputCacheKey, HtmlNode> {

    private static final String USER_AGENT_HEADER = "User-Agent";

    @Override
    public String getCacheName() {
        return "output";
    }

    @Override
    public Object getSpecificKey(CompositeOutputCacheKey keyBase, Object... keyParams) {
        HttpServletRequest request = keyBase.getRequest();
        return getKey(keyBase.getPageId(),
                keyBase.getName(),
                keyBase.getMvcData(),
                keyBase.getInclude(),
                request == null ? "" : request.getHeader(USER_AGENT_HEADER));
    }
}

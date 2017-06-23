package com.sdl.dxa.caching.wrapper;

import com.sdl.webapp.common.api.model.MvcData;
import lombok.Value;

import javax.servlet.http.HttpServletRequest;

/**
 * Composite key (DTO) for output caching.
 */
@Value
public class CompositeOutputCacheKey {

    private String pageId;

    private String name;

    private String include;

    private MvcData mvcData;

    private HttpServletRequest request;
}

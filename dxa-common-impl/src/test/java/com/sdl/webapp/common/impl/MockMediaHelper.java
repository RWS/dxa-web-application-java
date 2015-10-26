package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockMediaHelper extends AbstractMediaHelper {

    @Autowired
    public MockMediaHelper(WebRequestContext webRequestContext) {
        super(webRequestContext);
    }

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        return url;
    }
}

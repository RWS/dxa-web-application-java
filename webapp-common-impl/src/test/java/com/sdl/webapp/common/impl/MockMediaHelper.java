package com.sdl.webapp.common.impl;

import org.springframework.stereotype.Component;

@Component
public class MockMediaHelper extends AbstractMediaHelper {

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        return url;
    }
}

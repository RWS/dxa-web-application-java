package com.sdl.webapp.common.impl;

import org.springframework.stereotype.Component;

/**
 * Mock implementation of {@code WebRequestContext} for testing.
 */
@Component
public class MockWebRequestContext extends WebRequestContextImpl {

    private int displayWidth;
    private double pixelRatio;
    private int maxMediaWidth;

    @Override
    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    @Override
    public double getPixelRatio() {
        return pixelRatio;
    }

    public void setPixelRatio(double pixelRatio) {
        this.pixelRatio = pixelRatio;
    }

    @Override
    public int getMaxMediaWidth() {
        return maxMediaWidth;
    }

    public void setMaxMediaWidth(int maxMediaWidth) {
        this.maxMediaWidth = maxMediaWidth;
    }
}

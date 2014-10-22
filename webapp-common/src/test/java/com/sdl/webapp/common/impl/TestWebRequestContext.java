package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.ScreenWidth;
import org.springframework.stereotype.Component;

@Component
public class TestWebRequestContext extends WebRequestContextImpl {

    private ScreenWidth screenWidth;
    private int displayWidth;
    private double pixelRatio;
    private int maxMediaWidth;

    @Override
    public ScreenWidth getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(ScreenWidth screenWidth) {
        this.screenWidth = screenWidth;
    }

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

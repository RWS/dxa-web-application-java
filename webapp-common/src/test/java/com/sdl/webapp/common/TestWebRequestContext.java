package com.sdl.webapp.common;

import com.sdl.webapp.common.config.ScreenWidth;
import com.sdl.webapp.common.config.WebRequestContext;
import org.springframework.stereotype.Component;

@Component
public class TestWebRequestContext extends WebRequestContext {

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

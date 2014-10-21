package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.Localization;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Implementation of {@code WebRequestContext}.
 *
 * This implementation gets information about the display width etc. from the Ambient Data Framework.
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebRequestContextImpl implements WebRequestContext {

    private static final URI URI_BROWSER_DISPLAY_WIDTH = URI.create("taf:claim:context:browser:displayWidth");
    private static final URI URI_DEVICE_PIXEL_RATIO = URI.create("taf:claim:context:device:pixelRatio");

    private static final int MAX_WIDTH = 1024;

    @Autowired
    private MediaHelper mediaHelper;

    private Localization localization;

    private ScreenWidth screenWidth;
    private Integer displayWidth;
    private Double pixelRatio;
    private Integer maxMediaWidth;

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public ScreenWidth getScreenWidth() {
        if (screenWidth == null) {
            final int displayWidth = getDisplayWidth();
            if (displayWidth < mediaHelper.getSmallScreenBreakpoint()) {
                screenWidth = ScreenWidth.EXTRA_SMALL;
            } else if (displayWidth < mediaHelper.getMediumScreenBreakpoint()) {
                screenWidth = ScreenWidth.SMALL;
            } else if (displayWidth < mediaHelper.getLargeScreenBreakpoint()) {
                screenWidth = ScreenWidth.MEDIUM;
            } else {
                screenWidth = ScreenWidth.LARGE;
            }
        }

        return screenWidth;
    }

    public int getDisplayWidth() {
        if (displayWidth == null) {
            final ClaimStore currentClaimStore = AmbientDataContext.getCurrentClaimStore();
            displayWidth = (int) currentClaimStore.get(URI_BROWSER_DISPLAY_WIDTH);
        }
        return displayWidth;
    }

    public double getPixelRatio() {
        if (pixelRatio == null) {
            pixelRatio = (double) AmbientDataContext.getCurrentClaimStore().get(URI_DEVICE_PIXEL_RATIO);
        }
        return pixelRatio;
    }

    public int getMaxMediaWidth() {
        if (maxMediaWidth == null) {
            maxMediaWidth = (int) (Math.max(1.0, getPixelRatio()) * Math.min(getDisplayWidth(), MAX_WIDTH));
        }
        return maxMediaWidth;
    }
}

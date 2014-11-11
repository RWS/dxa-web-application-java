package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
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

    private String baseUrl;
    private String requestUrl;

    private Localization localization;

    private Integer displayWidth;
    private Double pixelRatio;
    private Integer maxMediaWidth;

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getRequestUrl() {
        return requestUrl;
    }

    @Override
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    @Override
    public boolean isPreview() {
        // Should return true if the request is from XPM (NOTE currently always true for staging as we cannot reliably
        // distinguish XPM requests)
        return localization.isStaging();
    }

    @Override
    public int getDisplayWidth() {
        if (displayWidth == null) {
            final ClaimStore currentClaimStore = AmbientDataContext.getCurrentClaimStore();
            displayWidth = 1024; // (int) currentClaimStore.get(URI_BROWSER_DISPLAY_WIDTH);
            // TODO: See [C#] BrowserClaims.DisplayWidth - special hack to make the default 1024 instead of 800

        }
        return displayWidth;
    }

    @Override
    public double getPixelRatio() {
        if (pixelRatio == null) {
            pixelRatio = (double) AmbientDataContext.getCurrentClaimStore().get(URI_DEVICE_PIXEL_RATIO);
        }
        return pixelRatio;
    }

    @Override
    public int getMaxMediaWidth() {
        if (maxMediaWidth == null) {
            maxMediaWidth = (int) (Math.max(1.0, getPixelRatio()) * Math.min(getDisplayWidth(), MAX_WIDTH));
        }
        return maxMediaWidth;
    }
}

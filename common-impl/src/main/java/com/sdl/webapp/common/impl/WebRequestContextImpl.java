package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(WebRequestContextImpl.class);

    private static final URI URI_BROWSER_DISPLAY_WIDTH = URI.create("taf:claim:context:browser:displayWidth");
    private static final URI URI_DEVICE_PIXEL_RATIO = URI.create("taf:claim:context:device:pixelRatio");

    private static final int DEFAULT_WIDTH = 1024;
    private static final int MAX_WIDTH = 1024;

    private String baseUrl;
    private String contextPath;
    private String requestPath;

    private boolean contextCookiePresent;

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
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    @Override
    public String getFullUrl() {
        return baseUrl + contextPath + requestPath;
    }

    @Override
    public boolean isContextCookiePresent() {
        return contextCookiePresent;
    }

    @Override
    public void setContextCookiePresent(boolean present) {
        this.contextCookiePresent = present;
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
            displayWidth = (Integer) currentClaimStore.get(URI_BROWSER_DISPLAY_WIDTH);
            if (displayWidth == null) {
                displayWidth = DEFAULT_WIDTH;
            }

            // NOTE: The context engine uses a default browser width of 800, which we override to 1024
            if (displayWidth == 800 && isContextCookiePresent()) {
                displayWidth = DEFAULT_WIDTH;
            }
        }
        return displayWidth;
    }

    @Override
    public double getPixelRatio() {
        if (pixelRatio == null) {
            pixelRatio = (Double) AmbientDataContext.getCurrentClaimStore().get(URI_DEVICE_PIXEL_RATIO);
            if (pixelRatio == null) {
                pixelRatio = 1.0;
                LOG.debug("Pixel ratio ADF claim not available - using default value: {}", pixelRatio);
            }
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

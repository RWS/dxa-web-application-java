package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.impl.contextengine.BrowserClaims;
import com.sdl.webapp.common.impl.contextengine.DeviceClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Stack;


/**
 * Implementation of {@code WebRequestContext}.
 * <p/>
 * This implementation gets information about the display width etc. from the Ambient Data Framework.
 */
@Component
@Primary
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebRequestContextImpl implements WebRequestContext {
    private static final Logger LOG = LoggerFactory.getLogger(WebRequestContextImpl.class);
    private static final int DEFAULT_WIDTH = 1024;
    private static final int MAX_WIDTH = 1024;
    @Autowired
    private MediaHelper mediaHelper;
    private Localization localization;
    private boolean hasNoLocalization;
    private Integer maxMediaWidth;
    private Double pixelRatio;
    private ScreenWidth screenwidth;
    private boolean contextCookiePresent;
    private Integer displayWidth;
    private String baseUrl;
    private String contextPath;
    private String requestPath;
    private String pageId;
    private Boolean isDeveloperMode;
    private Boolean isInclude;
    private Stack<Integer> containerSizeStack = new Stack<>();

    @Autowired
    private ContextEngine contextEngine;
    private Stack<RegionModel> parentRegionstack = new Stack<>();

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    @Override
    public boolean getHasNoLocalization() {
        return hasNoLocalization;
    }

    @Override
    public void setHasNoLocalization(boolean value) {
        hasNoLocalization = value;
    }

    @Override
    public int getMaxMediaWidth() {
        if (maxMediaWidth == null) {
            maxMediaWidth = (int) (Math.max(1.0, getPixelRatio()) * Math.min(getDisplayWidth(), MAX_WIDTH));
        }
        return maxMediaWidth;
    }

    @Override
    public double getPixelRatio() {
        if (pixelRatio == null) {
            pixelRatio = this.getContextEngine().getClaims(DeviceClaims.class).getPixelRatio();
            if (pixelRatio == null) {
                pixelRatio = 1.0;
                LOG.debug("Pixel ratio ADF claim not available - using default value: {}", pixelRatio);
            }
        }
        return pixelRatio;
    }

    public ScreenWidth getScreenWidth() {
        if (screenwidth == null) {
            screenwidth = calculateScreenWidth();
        }
        return screenwidth;
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
    public ContextEngine getContextEngine() {
        return this.contextEngine;
    }

    @Override
    public String getPageId() {
        return pageId;
    }

    @Override
    public void setPageId(String value) {
        this.pageId = value;
    }

    @Override
    public boolean isDeveloperMode() {
        if (this.isDeveloperMode == null) {
            this.isDeveloperMode = getIsDeveloperMode();
        }
        return this.isDeveloperMode;
    }

    private boolean getIsDeveloperMode() {
        return this.isDeveloperMode;
    }

    @Override
    public void setIsDeveloperMode(boolean value) {
        this.isDeveloperMode = value;
    }

    @Override
    public boolean getIsInclude() {
        return this.isInclude;
    }

    @Override
    public void setIsInclude(boolean value) {
        this.isInclude = value;
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

            this.displayWidth = this.getContextEngine().getClaims(BrowserClaims.class).getDisplayWidth();
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

    protected ScreenWidth calculateScreenWidth() {
        int width = isContextCookiePresent() ? this.getDisplayWidth() : MAX_WIDTH;
        if (width < this.mediaHelper.getSmallScreenBreakpoint()) {
            return ScreenWidth.EXTRA_SMALL;
        }
        if (width < this.mediaHelper.getMediumScreenBreakpoint()) {
            return ScreenWidth.SMALL;
        }
        if (width < this.mediaHelper.getLargeScreenBreakpoint()) {
            return ScreenWidth.MEDIUM;
        }
        return ScreenWidth.LARGE;
    }

    @Override
    public int getContainerSize() {
        return this.containerSizeStack.peek();
    }

    @Override
    public void popContainerSize() {
        this.containerSizeStack.pop();
    }

    @Override
    public void pushContainerSize(int containerSize) {

        if (containerSize == 0) {
            containerSize = this.mediaHelper.getGridSize();
        }
        if (this.containerSizeStack.size() > 0) {
            int parentContainerSize = this.containerSizeStack.peek();
            containerSize = containerSize * parentContainerSize / this.mediaHelper.getGridSize();
        }
        this.containerSizeStack.push(containerSize);
    }

    @Override
    public RegionModel getParentRegion() {
        if (!parentRegionstack.isEmpty()) {
            return this.parentRegionstack.peek();
        }
        return null;
    }

    @Override
    public void popParentRegion() {
        this.parentRegionstack.pop();
    }

    @Override
    public void pushParentRegion(RegionModel parentRegion) {
        this.parentRegionstack.push(parentRegion);
    }
}

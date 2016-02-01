package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.impl.contextengine.BrowserClaims;
import com.sdl.webapp.common.impl.contextengine.DeviceClaims;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Stack;


/**
 * Implementation of {@code WebRequestContext}.
 * <p/>
 * This implementation gets information about the display width etc. from the Ambient Data Framework.
 */
@Component
@Primary
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Setter
@Getter
public class WebRequestContextImpl implements WebRequestContext {
    private static final Logger LOG = LoggerFactory.getLogger(WebRequestContextImpl.class);
    private static final int DEFAULT_WIDTH = 1024;
    private static final int MAX_WIDTH = 1024;

    @Autowired
    private MediaHelper mediaHelper;

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private ContextEngine contextEngine;

    private Stack<RegionModel> parentRegionstack = new Stack<>();
    private Localization localization;
    private boolean noLocalization;
    private Integer maxMediaWidth;
    private Double pixelRatio;
    private ScreenWidth screenwidth;
    private boolean contextCookiePresent;
    private Integer displayWidth;
    private String baseUrl;
    private String contextPath;
    private String requestPath;
    private String pageId;
    private boolean developerMode;
    private boolean include;
    private Stack<Integer> containerSizeStack = new Stack<>();

    @Override
    public String getFullUrl() {
        return baseUrl + contextPath + requestPath;
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

    @Override
    public ScreenWidth getScreenWidth() {
        if (screenwidth == null) {
            screenwidth = calculateScreenWidth();
        }
        return screenwidth;
    }

    @Override
    public boolean isDeveloperMode() {
        return this.developerMode;
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

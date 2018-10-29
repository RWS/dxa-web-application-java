package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationNotFoundException;
import com.sdl.webapp.common.api.localization.LocalizationNotResolvedException;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.localization.LocalizationResolverException;
import com.sdl.webapp.common.api.localization.UnknownLocalizationHandler;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.impl.contextengine.BrowserClaims;
import com.sdl.webapp.common.impl.contextengine.DeviceClaims;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Stack;

/**
 * This implementation gets information about the display width etc. from the Ambient Data Framework.
 */
@Slf4j
@Primary
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebRequestContextImpl implements WebRequestContext {

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Autowired
    private MediaHelper mediaHelper;

    @Autowired
    private HttpServletRequest servletRequest;

    @Getter(lazy = true)
    private final boolean include = include();

    @Getter(lazy = true)
    private final String contextPath = contextPath();

    @Getter(lazy = true)
    private final String requestPath = requestPath();

    @Getter(lazy = true)
    private final String baseUrl = baseUrl();

    @Getter(lazy = true)
    private final boolean developerMode = developerMode();

    @Getter(lazy = true)
    private final boolean contextCookiePresent = contextCookiePresent();

    @Autowired
    private ContextEngine contextEngine;

    @Getter(lazy = true)
    private final int displayWidth = displayWidth();

    @Getter(lazy = true)
    private final double pixelRatio = pixelRatio();

    @Autowired
    private LocalizationResolver localizationResolver;

    @Autowired(required = false)
    private UnknownLocalizationHandler unknownLocalizationHandler;

    @Getter(lazy = true)
    private final Localization localization = localization();

    @Getter(lazy = true)
    private final Boolean noLocalization = noLocalization();

    @Getter
    @Setter
    private PageModel page;

    private Stack<RegionModel> parentRegionStack = new Stack<>();

    private Stack<Integer> containerSizeStack = new Stack<>();

    @Override
    public String getFullUrl() {
        return getBaseUrl() + getContextPath() + getRequestPath();
    }

    @Override
    public boolean isPreview() {
        // Should return true if the request is from XPM (NOTE currently always true for staging as we cannot reliably
        // distinguish XPM requests)
        return getLocalization().isStaging();
    }

    @Override
    public ScreenWidth getScreenWidth() {
        int width = isContextCookiePresent() ? getDisplayWidth() : Width.MAX_WIDTH;

        if (width < mediaHelper.getSmallScreenBreakpoint()) {
            return ScreenWidth.EXTRA_SMALL;
        }

        if (width < mediaHelper.getMediumScreenBreakpoint()) {
            return ScreenWidth.SMALL;
        }

        if (width < mediaHelper.getLargeScreenBreakpoint()) {
            return ScreenWidth.MEDIUM;
        }

        return ScreenWidth.LARGE;
    }

    @Override
    public int getMaxMediaWidth() {
        return (int) Math.max(1.0, getPixelRatio()) * Math.min(getDisplayWidth(), Width.MAX_WIDTH);
    }

    @Override
    public int getContainerSize() {
        return containerSizeStack.peek();
    }

    @Override
    public void pushContainerSize(int containerSize) {
        int size = containerSize;
        if (containerSize == 0) {
            size = mediaHelper.getGridSize();
        }
        if (!containerSizeStack.isEmpty()) {
            int parentContainerSize = containerSizeStack.peek();
            size = containerSize * parentContainerSize / mediaHelper.getGridSize();
        }
        containerSizeStack.push(size);
    }

    @Override
    public void popContainerSize() {
        containerSizeStack.pop();
    }

    @Override
    public RegionModel getParentRegion() {
        return parentRegionStack.isEmpty() ? null : parentRegionStack.peek();
    }

    @Override
    public void pushParentRegion(RegionModel parentRegion) {
        parentRegionStack.push(parentRegion);
    }

    @Override
    public void popParentRegion() {
        parentRegionStack.pop();
    }

    private String baseUrl() {
        return servletRequest.getRequestURL().toString().replaceFirst(servletRequest.getRequestURI() + "$", "");
    }

    private String contextPath() {
        return urlPathHelper.getOriginatingContextPath(servletRequest);
    }

    private String requestPath() {
        return urlPathHelper.getPathWithinApplication(servletRequest);
    }

    private boolean developerMode() {
        return servletRequest.getServerName().contains("localhost");
    }

    private boolean contextCookiePresent() {
        final Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("context".equals(cookie.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean noLocalization() {
        return getLocalization() == null;
    }

    private Localization localization() {
        Localization resolveLocalization = null;
        try {
            resolveLocalization = localizationResolver.getLocalization(getFullUrl());
        } catch (LocalizationResolverException e) {
            if (unknownLocalizationHandler != null) {
                log.warn("Localization is not resolved for " + getFullUrl() + ", Localization handler is set, trying to resolve using it ", e);
                resolveLocalization = unknownLocalizationHandler.handleUnknown(e, servletRequest);
                if (resolveLocalization == null) {
                    log.error("Unknown Localization handler is set but localization wasn't resolved with it, fallback", e);
                    LocalizationNotResolvedException fallbackException = unknownLocalizationHandler.getFallbackException(e, servletRequest);
                    if (fallbackException != null) {
                        throw fallbackException;
                    }
                    log.error("Fallback exception from Unknown Localization Handler is null, fallback to default handling.", e);
                }
            }
        }
        if (resolveLocalization == null) {
            throw new LocalizationNotFoundException("Localization not found for " + getFullUrl());
        }
        if (log.isTraceEnabled()) {
            log.trace("Localization for {} is: [{}] {}", getFullUrl(), resolveLocalization.getId(), resolveLocalization.getPath());
        }
        return resolveLocalization;
    }

    public boolean include() {
        return servletRequest.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }

    private int displayWidth() {
        BrowserClaims claims = contextEngine.getClaims(BrowserClaims.class);
        if (claims == null) {
            return Width.DEFAULT_WIDTH;
        }

        Integer resolveDisplayWidth = claims.getDisplayWidth();
        if (resolveDisplayWidth == null) {
            return Width.DEFAULT_WIDTH;
        }

        // NOTE: The context engine uses a default browser width of 800, which we override to 1024
        if (resolveDisplayWidth == 800 && contextCookiePresent()) {
            return Width.DEFAULT_WIDTH;
        }

        return resolveDisplayWidth;
    }

    private double pixelRatio() {
        DeviceClaims claims = contextEngine.getClaims(DeviceClaims.class);

        Double resolvePixelRatio;
        if (claims == null || (resolvePixelRatio = claims.getPixelRatio()) == null) {
            resolvePixelRatio = 1.0;
            log.debug("Pixel ratio ADF claim not available - using default value: {}", resolvePixelRatio);
        }

        return resolvePixelRatio;
    }

    private static class Width {

        static final int DEFAULT_WIDTH = 1024;

        static final int MAX_WIDTH = 1024;

        private Width() {
        }
    }
}

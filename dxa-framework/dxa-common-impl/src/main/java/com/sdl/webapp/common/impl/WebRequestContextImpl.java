package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationNotFoundException;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.localization.LocalizationResolverException;
import com.sdl.webapp.common.api.localization.UnknownLocalizationHandler;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Stack;

/**
 * <p>This implementation gets information about the display width etc. from the Ambient Data Framework.</p>
 */
@Slf4j
@Primary
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebRequestContextImpl implements WebRequestContext {

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Autowired
    private MediaHelper mediaHelper;

    @Getter
    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private HttpServletResponse servletResponse;

    @Getter
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

    private SettableField<Boolean> include = new SettableField<>();

    private SettableField<String> contextPath = new SettableField<>();

    private SettableField<String> requestPath = new SettableField<>();

    private SettableField<String> baseUrl = new SettableField<>();

    private SettableField<Boolean> developerMode = new SettableField<>();

    private SettableField<Boolean> contextCookiePresent = new SettableField<>();

    private SettableField<Localization> localization = new SettableField<>();

    private SettableField<Boolean> noLocalization = new SettableField<>();

    @Getter
    @Setter
    private String pageId;

    private Stack<RegionModel> parentRegionStack = new Stack<>();

    private Stack<Integer> containerSizeStack = new Stack<>();

    @Override
    public String getBaseUrl() {
        if (this.baseUrl.isSet) {
            return this.baseUrl.value;
        }
        String newValue = baseUrl();
        setBaseUrl(newValue);
        return newValue;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl.value = baseUrl;
        this.baseUrl.isSet = true;
    }

    @Override
    public String getContextPath() {
        if (contextPath.isSet) {
            return contextPath.value;
        }
        String newValue = contextPath();
        setContextPath(newValue);
        return newValue;
    }

    @Override
    public void setContextPath(String contextPath) {
        this.contextPath.value = contextPath;
        this.contextPath.isSet = true;
    }

    @Override
    public String getRequestPath() {
        if (requestPath.isSet) {
            return requestPath.value;
        }
        String newValue = requestPath();
        setRequestPath(newValue);
        return newValue;
    }

    @Override
    public void setRequestPath(String requestPath) {
        this.requestPath.value = requestPath;
        this.requestPath.isSet = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullUrl() {
        return getBaseUrl() + getContextPath() + getRequestPath();
    }

    @Override
    public boolean isContextCookiePresent() {
        if (this.contextCookiePresent.isSet) {
            return this.contextCookiePresent.value;
        }
        boolean value = contextCookiePresent();
        setContextCookiePresent(value);
        return value;
    }

    @Override
    public void setContextCookiePresent(boolean present) {
        this.contextCookiePresent.value = present;
        this.contextCookiePresent.isSet = true;
    }

    @Override
    public Localization getLocalization() {
        if (localization.isSet) {
            return localization.value;
        }
        Localization value = localization();
        setLocalization(value);
        return value;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization.value = localization;
        this.localization.isSet = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPreview() {
        // Should return true if the request is from XPM (NOTE currently always true for staging as we cannot reliably
        // distinguish XPM requests)
        return getLocalization().isStaging();
    }

    @Override
    public ScreenWidth getScreenWidth() {
        return screenWidth();
    }

    @Override
    public int getMaxMediaWidth() {
        return maxMediaWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getContainerSize() {
        return containerSizeStack.peek();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushContainerSize(int containerSize) {
        if (containerSize == 0) {
            containerSize = mediaHelper.getGridSize();
        }
        if (!containerSizeStack.isEmpty()) {
            int parentContainerSize = containerSizeStack.peek();
            containerSize = containerSize * parentContainerSize / mediaHelper.getGridSize();
        }
        containerSizeStack.push(containerSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void popContainerSize() {
        containerSizeStack.pop();
    }

    @Override
    public boolean isNoLocalization() {
        if (noLocalization.isSet) {
            return noLocalization.value;
        }
        return getLocalization() == null;
    }

    @Override
    public void setNoLocalization(boolean value) {
        this.noLocalization.value = value;
        this.noLocalization.isSet = true;
    }

    @Override
    public boolean isDeveloperMode() {
        if (developerMode.isSet) {
            return developerMode.value;
        }
        boolean value = developerMode();
        setDeveloperMode(value);
        return value;
    }

    @Override
    public void setDeveloperMode(boolean value) {
        this.developerMode.value = value;
        this.developerMode.isSet = true;
    }

    @Override
    public boolean isInclude() {
        if (include.isSet) {
            return include.value;
        }

        boolean newValue = include();
        setInclude(newValue);
        return newValue;
    }

    @Override
    public void setInclude(boolean value) {
        include.value = value;
        include.isSet = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionModel getParentRegion() {
        return parentRegionStack.isEmpty() ? null : parentRegionStack.peek();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushParentRegion(RegionModel parentRegion) {
        parentRegionStack.push(parentRegion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void popParentRegion() {
        parentRegionStack.pop();
    }

    private String baseUrl() {
        String baseUrl = servletRequest.getRequestURL().toString();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private String contextPath() {
        return urlPathHelper.getOriginatingContextPath(servletRequest);
    }

    private String requestPath() {
        return urlPathHelper.getOriginatingRequestUri(servletRequest);
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

    private Localization localization() {
        Localization localization = null;
        try {
            localization = localizationResolver.getLocalization(getFullUrl());
        } catch (LocalizationResolverException e) {
            if (unknownLocalizationHandler != null) {
                localization = unknownLocalizationHandler.handleUnknown(e, servletRequest, servletResponse);
            }
            if (localization == null) {
                throw new LocalizationNotFoundException("Localization not found", e);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("Localization for {} is: [{}] {}", getFullUrl(), localization.getId(), localization.getPath());
        }
        return localization;
    }

    public boolean include() {
        return servletRequest.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }

    private int displayWidth() {
        Integer displayWidth = contextEngine.getClaims(BrowserClaims.class).getDisplayWidth();
        if (displayWidth == null) {
            return Width.defaultWidth;
        }

        // NOTE: The context engine uses a default browser width of 800, which we override to 1024
        if (displayWidth == 800 && contextCookiePresent()) {
            return Width.defaultWidth;
        }

        return displayWidth;
    }

    private ScreenWidth screenWidth() {
        int width = isContextCookiePresent() ? getDisplayWidth() : Width.maxWidth;

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

    private int maxMediaWidth() {
        return (int) Math.max(1.0, getPixelRatio()) * Math.min(getDisplayWidth(), Width.maxWidth);
    }

    private double pixelRatio() {
        Double pixelRatio = contextEngine.getClaims(DeviceClaims.class).getPixelRatio();
        if (pixelRatio == null) {
            pixelRatio = 1.0;
            log.debug("Pixel ratio ADF claim not available - using default value: {}", this.pixelRatio);
        }
        return pixelRatio;
    }

    private interface Width {

        int defaultWidth = 1024;

        int maxWidth = 1024;
    }

    //todo dxa2 replace with @Getter(lazy=true)
    private static class SettableField<T> {

        T value;

        boolean isSet;
    }
}

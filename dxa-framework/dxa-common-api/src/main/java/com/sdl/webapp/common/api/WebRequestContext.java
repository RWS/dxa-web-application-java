package com.sdl.webapp.common.api;

import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;

import javax.servlet.ServletRequest;

/**
 * Provides information relevant for the current request.
 */
public interface WebRequestContext {

    /**
     * Gets the base URL for the current request. The base URL consists of the protocol, server name, and port number.
     * It does not include the context path of the web application.
     *
     * @return The base URL for the current request.
     */
    String getBaseUrl();

    /**
     * Sets the base URL for the current request.
     *
     * @param baseUrl The base URL for the current request.
     */
    void setBaseUrl(String baseUrl);

    /**
     * Gets the context path of the web application.
     *
     * @return The context path of the web application.
     */
    String getContextPath();

    /**
     * Sets the context path of the web application.
     *
     * @param contextPath The context path of the web application.
     */
    void setContextPath(String contextPath);

    /**
     * Gets the request path of the current request. This path is relative to the context path of the web application.
     * If the current request is an include, this returns the path of the original request.
     *
     * @return The request path of the current request.
     */
    String getRequestPath();

    /**
     * Sets the request path of the current request.
     *
     * @param requestPath The request path of the current request.
     */
    void setRequestPath(String requestPath);

    /**
     * Gets the full URL of the current request, consisting of the base URL, context path and request path.
     *
     * @return The full URL of the current request.
     */
    String getFullUrl();

    boolean isContextCookiePresent();

    void setContextCookiePresent(boolean present);

    /**
     * Gets the localization of the current request.
     *
     * @return The localization of the current request.
     */
    Localization getLocalization();

    /**
     * Sets the localization of the current request. This is normally called by {@code LocalizationResolverInterceptor}.
     */
    void setLocalization(Localization localization);

    /**
     * Checks if the web application is in preview mode (when XPM is enabled).
     *
     * @return {@code true} when in preview mode, {@code false} otherwise.
     */
    boolean isPreview();

    /**
     * Gets the display width of the current request.
     *
     * @return The display width of the current request.
     */
    int getDisplayWidth();

    ScreenWidth getScreenWidth();

    /**
     * Gets the pixel ratio of the current request.
     *
     * @return The pixel ratio of the current request.
     */
    double getPixelRatio();

    /**
     * Gets the maximum media width of the current request.
     *
     * @return The maximum media width of the current request.
     */
    int getMaxMediaWidth();

    /**
     * Get current container size in the context of current region.
     *
     * @return container size
     */
    int getContainerSize();

    /**
     * Push container size for current context (rendering of a region or entity)
     *
     * @param containerSize
     */
    void pushContainerSize(int containerSize);

    /**
     * Pop container size back to previous context
     */
    void popContainerSize();

    /**
     * Gets the contextengine of the current request.
     *
     * @return The contextengine of the current request.
     */
    ContextEngine getContextEngine();

    // TODO: Should we have setters in this interface??

    boolean isNoLocalization();

    void setNoLocalization(boolean value);

    String getPageId();

    void setPageId(String value);

    boolean isDeveloperMode();

    void setDeveloperMode(boolean value);

    boolean isInclude();

    void setInclude(boolean value);

    RegionModel getParentRegion();

    void pushParentRegion(RegionModel value);

    void popParentRegion();

    ServletRequest getServletRequest();
}

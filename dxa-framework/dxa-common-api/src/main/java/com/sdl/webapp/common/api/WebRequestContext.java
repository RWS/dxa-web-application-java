package com.sdl.webapp.common.api;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;

/**
 * Provides information relevant for the current request.
 *
 * @dxa.publicApi
 */
public interface WebRequestContext {

    /**
     * Gets the base URL for the current request. The base URL consists of the protocol, server name, and port number.
     * It does not include the context path of the web application. Does not end with a slash.
     *
     * @return The base URL for the current request.
     */
    String getBaseUrl();

    /**
     * Gets the context path of the web application in a servlet container. For root applications return an empty string.
     * Does not end with a slash.
     *
     * @return The context path of the web application.
     */
    String getContextPath();

    /**
     * Gets the request path of the current request. This path is relative to the context path of the web application.
     * If the current request is an include, this returns the path of the original request. Does not end with a slash.
     *
     * @return The request path of the current request.
     */
    String getRequestPath();

    /**
     * Gets the full URL of the current request, consisting of the base URL, context path and request path.
     *
     * @return The full URL of the current request.
     */
    String getFullUrl();

    boolean isContextCookiePresent();

    /**
     * Gets the localization of the current request.
     *
     * @return The localization of the current request.
     */
    Localization getLocalization();

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
     * @param containerSize a int.
     */
    void pushContainerSize(int containerSize);

    /**
     * Pop container size back to previous context
     */
    void popContainerSize();

    /**
     * Return the current page model.
     *
     * @return the current page model
     */
    PageModel getPage();

    @Deprecated
    void setPage(PageModel page);

    boolean isDeveloperMode();

    boolean isInclude();

    RegionModel getParentRegion();

    void pushParentRegion(RegionModel value);

    void popParentRegion();

}

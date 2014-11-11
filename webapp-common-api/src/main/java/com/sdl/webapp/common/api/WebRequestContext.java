package com.sdl.webapp.common.api;

import com.sdl.webapp.common.api.localization.Localization;

/**
 * Provides information relevant for the current request.
 */
public interface WebRequestContext {

    /**
     * Gets the base URL for the current request.
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
     * Gets the full URL for the current request.
     *
     * @return The full URL for the current request.
     */
    String getRequestUrl();

    /**
     * Sets the full URL for the current request.
     *
     * @param requestUrl The full URL for the current request.
     */
    void setRequestUrl(String requestUrl);

    /**
     * Gets the localization for the current request.
     *
     * @return The localization for the current request.
     */
    Localization getLocalization();

    /**
     * Sets the localization for the current request. This is normally called by {@code LocalizationResolverInterceptor}.
     *
     * @return The localization for the current request.
     */
    void setLocalization(Localization localization);

    /**
     * Checks if the web application is in preview mode (when XPM is enabled).
     *
     * @return {@code true} when in preview mode, {@code false} otherwise.
     */
    boolean isPreview();

    /**
     * Gets the display width for the current request.
     *
     * @return The display width for the current request.
     */
    int getDisplayWidth();

    /**
     * Gets the pixel ratio for the current request.
     *
     * @return The pixel ratio for the current request.
     */
    double getPixelRatio();

    /**
     * Gets the maximum media width for the current request.
     *
     * @return The maximum media width for the current request.
     */
    int getMaxMediaWidth();
}

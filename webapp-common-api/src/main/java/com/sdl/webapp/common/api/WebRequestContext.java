package com.sdl.webapp.common.api;

import com.sdl.webapp.common.api.localization.Localization;

/**
 * Provides information relevant for the current request.
 */
public interface WebRequestContext {

    /**
     * Gets the localization for the current request.
     *
     * @return The localization for the current request.
     */
    Localization getLocalization();

    /**
     * Check if the web application is in preview mode (when XPM is enabled).
     *
     * @return {@code true} when in preview mode, {@code false} otherwise.
     */
    boolean isPreview();

    /**
     * Sets the localization for the current request. This is normally called by {@code LocalizationResolverInterceptor}.
     *
     * @return The localization for the current request.
     */
    void setLocalization(Localization localization);

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

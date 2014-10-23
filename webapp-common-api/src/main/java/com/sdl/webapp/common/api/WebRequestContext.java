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
     * Sets the localization for the current request. This is normally called by {@code LocalizationResolverInterceptor}.
     *
     * @return The localization for the current request.
     */
    void setLocalization(Localization localization);

    /**
     * Gets the screen width for the current request.
     *
     * @return The screen width for the current request.
     */
    ScreenWidth getScreenWidth();

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

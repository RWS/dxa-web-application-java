package com.sdl.webapp.common.api.localization;

/**
 * Localization resolver.
 */
public interface LocalizationResolver {

    /**
     * Gets the localization for a specified URL.
     *
     * @param url The URL.
     * @return The {@code Localization} for this URL.
     * @throws LocalizationResolverException If an error occurred so that the localization could not be determined.
     */
    Localization getLocalization(String url) throws LocalizationResolverException;

    /**
     * Refreshes the specified localization, so that its configuration is reloaded.
     *
     * @param localizationId The localization ID of the localization to refresh.
     */
    void refreshLocalization(String localizationId);
}

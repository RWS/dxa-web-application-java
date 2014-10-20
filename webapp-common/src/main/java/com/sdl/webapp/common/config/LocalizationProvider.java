package com.sdl.webapp.common.config;

import java.io.IOException;

/**
 * Provider of {@code Localization} objects.
 */
public interface LocalizationProvider {

    /**
     * Get the {@code Localization} for the specified URL.
     *
     * @param url The URL.
     * @return The {@code Localization} for the specified URL.
     * @throws IOException If an I/O error occurred.
     */
    Localization getLocalization(String url) throws IOException;
}

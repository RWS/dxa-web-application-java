package com.sdl.webapp.common.api;

/**
 * Content provider for static content.
 */
public interface StaticContentProvider {

    /**
     * Gets a static content item by URL for a specific localization.
     *
     * @param url The URL of the static content item.
     * @param localization The localization.
     * @return The {@code StaticContentItem}.
     * @throws ContentProviderException If an error occurred so that the static content item could not be retrieved.
     */
    StaticContentItem getStaticContent(String url, Localization localization) throws ContentProviderException;
}

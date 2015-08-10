package com.sdl.webapp.common.api.content;

/**
 * Content provider for static content.
 */
public interface StaticContentProvider {

    /**
     * Gets a static content item by path for a specific localization.
     *
     * @param path The path of the static content item.
     * @param localizationId The localization ID.
     * @param localizationPath The localization path.
     * @return The {@code StaticContentItem}.
     * @throws ContentProviderException If an error occurred so that the static content item could not be retrieved.
     */
    StaticContentItem getStaticContent(String path, String localizationId, String localizationPath)
            throws ContentProviderException;
}

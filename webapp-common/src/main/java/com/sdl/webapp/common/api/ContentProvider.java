package com.sdl.webapp.common.api;

import com.sdl.webapp.common.api.model.Page;

import java.io.InputStream;

/**
 * Content provider.
 */
public interface ContentProvider {

    /**
     * Gets a page by URL for a specific localization.
     *
     * @param url The URL of the page.
     * @param localization The localization.
     * @return The {@code Page}.
     * @throws ContentProviderException If an error occurred so that the content of the page could be retrieved.
     */
    Page getPageModel(String url, Localization localization) throws ContentProviderException;

    /**
     * Gets an {@code InputStream} from which the raw content of a page can be read.
     *
     * @param url The URL of the page.
     * @param localization The localization.
     * @return An {@code InputStream} from which the raw content of a page can be read.
     * @throws ContentProviderException If an error occurred so that the content of the page could be retrieved.
     */
    InputStream getPageContent(String url, Localization localization) throws ContentProviderException;

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

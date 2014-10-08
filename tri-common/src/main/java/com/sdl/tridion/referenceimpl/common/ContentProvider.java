package com.sdl.tridion.referenceimpl.common;

import com.sdl.tridion.referenceimpl.common.model.Page;

/**
 * Content provider.
 */
public interface ContentProvider {

    /**
     * Gets the page for the specified URL.
     *
     * @param url The URL.
     * @return The page for the specified URL.
     * @throws ContentProviderException If the page could not be retrieved.
     */
    Page getPage(String url) throws ContentProviderException;
}

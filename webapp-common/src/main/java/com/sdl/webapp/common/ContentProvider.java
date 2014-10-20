package com.sdl.webapp.common;

import com.sdl.webapp.common.model.Page;
import com.sdl.webapp.common.model.entity.ContentList;
import com.sdl.webapp.common.model.entity.Teaser;

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

    void populateDynamicList(ContentList<Teaser> list);

}

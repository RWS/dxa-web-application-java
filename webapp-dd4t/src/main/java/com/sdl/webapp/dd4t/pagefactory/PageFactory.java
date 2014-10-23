package com.sdl.webapp.dd4t.pagefactory;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Page;
import org.dd4t.contentmodel.GenericPage;

/**
 * Page factory.
 */
public interface PageFactory {

    /**
     * Creates a page from a DD4T {@code GenericPage}.
     *
     * @param genericPage The {@code GenericPage}.
     * @param localization The localization.
     * @return The new page.
     * @throws ContentProviderException If an error occurs and the page cannot be created.
     */
    Page createPage(GenericPage genericPage, Localization localization) throws ContentProviderException;
}

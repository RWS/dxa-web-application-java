package com.sdl.webapp.common.api.content;

import com.sdl.dxa.common.dto.ClaimHolder;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementors of this interface are capable to provide content by the definitive query (e.g. path of a page or TCM URI).
 *
 * These are the functions that are new with DXA 2.2
 *
 * @since DXA 2.2
 * @dxa.publicApi
 */
public interface Dxa22ContentProvider extends ContentProvider {

    /**
     * Gets a {@link PageModel} by id for a specific localization.
     *
     * @param pageId         the id of the page requested
     * @param localization the current localization
     * @since DXA 2.2
     * @return the {@link PageModel} instance or null if page not found
     * @throws ContentProviderException if an error occurred so that the content of the page could be retrieved
     */
    @Nullable
    PageModel getPageModel(int pageId, Localization localization) throws ContentProviderException;

    /**
     * Gets a static content item by path for a specific localization.
     *
     * @param path             The path of the static content item.
     * @param localization     The localization.
     * @since DXA 2.2
     * @return The {@link StaticContentItem}.
     * @throws ContentProviderException If an error occurred so that the static content item could not be retrieved.
     */
    @NotNull StaticContentItem getStaticContent(String path, Localization localization) throws ContentProviderException;

    /**
     * Gets a static content binary item by its id for a specific localization.
     *
     * @param binaryId         The id of the static content item.
     * @param localization     The localization.
     * @since DXA 2.2
     * @return The {@link StaticContentItem}.
     * @throws ContentProviderException If an error occurred so that the static content item could not be retrieved.
     */
    StaticContentItem getStaticContent(int binaryId, Localization localization) throws ContentProviderException;

}

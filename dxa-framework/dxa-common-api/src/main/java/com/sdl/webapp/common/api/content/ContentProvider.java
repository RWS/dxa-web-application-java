package com.sdl.webapp.common.api.content;

import com.sdl.dxa.common.dto.ClaimHolder;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementors of this interface are capable to provide content by the definitive query (e.g. path of a page or TCM URI).
 *
 * @dxa.publicApi
 */
public interface ContentProvider {

    /**
     * Gets a {@link PageModel} by path for a specific localization.
     *
     * @param path         the path of the page requested
     * @param localization the current localization
     * @return the {@link PageModel} instance or null if page not found
     * @throws ContentProviderException if an error occurred so that the content of the page could be retrieved
     */
    @Nullable
    PageModel getPageModel(String path, Localization localization) throws ContentProviderException;

    /**
     * Gets a {@link PageModel} by id for a specific localization.
     *
     * @param pageId         the id of the page requested
     * @param localization the current localization
     * @return the {@link PageModel} instance or null if page not found
     * @throws ContentProviderException if an error occurred so that the content of the page could be retrieved
     */
    @Nullable
    PageModel getPageModel(int pageId, Localization localization, ClaimHolder claims) throws ContentProviderException;

    /**
     * Gets an {@link EntityModel} model by TCM URI for a specific localization.
     *
     * @param tcmUri       the TCM URI of entity model
     * @param localization the localization to get en entity from
     * @return the {@link EntityModel} instance
     * @throws ContentProviderException if an error occurred so that the content of the page could be retrieved
     * @throws DxaException             dxaException if an error occurred in DXA so that the content of the page could be retrieved
     * @throws IllegalArgumentException if tcmUri parameter is not well-formed
     */
    EntityModel getEntityModel(String tcmUri, Localization localization) throws DxaException;

    /**
     * Populates a dynamic list.
     *
     * @param dynamicList  the list to populate
     * @param localization the current localization
     * @throws ContentProviderException if an error occurred so that the content of the list could not be retrieved
     */
    <T extends EntityModel> void populateDynamicList(DynamicList<T, SimpleBrokerQuery> dynamicList, Localization localization) throws ContentProviderException;

    /**
     * Gets a static content item by path for a specific localization.
     *
     * Path to media resource like '/media/company-news-placeholder_tcm5-286_w1024_n.png'
     * or even '/media/wall_tcm5-308.jpg' where
     * tcm - namespace,
     * 5 - publicaionId
     * 286 - binaryId
     *
     * @param path             The path of the static content item.
     * @param localizationId   The localization ID.
     * @param localizationPath The localization path.
     * @return The {@link StaticContentItem}.
     * @throws ContentProviderException If an error occurred so that the static content item could not be retrieved.
     */
    @NotNull StaticContentItem getStaticContent(String path, String localizationId, String localizationPath) throws ContentProviderException;

    @NotNull StaticContentItem getStaticContent(String namespace, String path, String localizationId, String localizationPath) throws ContentProviderException;

    /**
     * Gets a static content binary item by its id for a specific localization.
     *
     * @param contentNamespace The namespace (ish for Docs, tcm for Sites)
     * @param binaryId         The id of the static content item.
     * @param localizationId   The localization ID.
     * @param localizationPath The localization path.
     * @return The {@link StaticContentItem}.
     * @throws ContentProviderException If an error occurred so that the static content item could not be retrieved.
     */
    StaticContentItem getStaticContent(String contentNamespace, int binaryId, String localizationId, String localizationPath) throws ContentProviderException;

}

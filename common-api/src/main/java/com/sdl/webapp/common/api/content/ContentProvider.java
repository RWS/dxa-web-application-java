package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.entity.ContentList;
import org.springframework.cglib.core.Local;

import java.io.InputStream;

/**
 * Content provider.
 */
public interface ContentProvider {

    /**
     * Gets a page by path for a specific localization.
     *
     * @param path The path of the page.
     * @param localization The localization.
     * @return The {@code Page}.
     * @throws ContentProviderException If an error occurred so that the content of the page could be retrieved.
     */
    Page getPageModel(String path, Localization localization) throws ContentProviderException;

    /**
     * Get entity model by TCM URI and specific template
     *
     * @param id
     * @param templateId
     * @param localization
     * @return  the {@code Entity}
     * @throws ContentProviderException
     */
    // TODO: What terminology should we use here? Is template id correct wording???
    Entity getEntityModel(String id, String templateId, Localization localization) throws ContentProviderException;

    /**
     * Gets an {@code InputStream} from which the raw content of a page can be read.
     *
     * @param path The path of the page.
     * @param localization The localization.
     * @return An {@code InputStream} from which the raw content of a page can be read.
     * @throws ContentProviderException If an error occurred so that the content of the page could be retrieved.
     */
    InputStream getPageContent(String path, Localization localization) throws ContentProviderException;

    /**
     * Populates a dynamic list.
     *
     * @param contentList The list to populate.
     * @param localization The localization.
     * @throws ContentProviderException If an error occurred so that the content of the list could not be retrieved.
     */
    void populateDynamicList(ContentList contentList, Localization localization) throws ContentProviderException;
}

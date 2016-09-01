package com.sdl.webapp.common.util;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static com.sdl.webapp.common.util.FileUtils.hasExtension;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Localization utils class holds helper-methods for the logic related to CM and {@link Localization} operations.
 */
@Slf4j
public final class LocalizationUtils {

    static final String DEFAULT_PAGE_NAME = "index";

    static final String DEFAULT_PAGE_EXTENSION = ".html";

    /**
     * Retrieves the schema ID from the localization configuration by the schema key.
     *
     * @param schemaKey    schema key as a name of configuration property,
     *                     should be either a content type (e.g. <code>"json"</code>)
     *                     or schema name + content type (e.g. "dev.json")
     * @param localization current localization
     * @return gets a schema key from localization, in case of fail returns <code>0</code>
     */
    public static int schemaIdFromSchemaKey(@NotNull String schemaKey, @NotNull Localization localization) {
        final String[] parts = schemaKey.split("\\.");
        if (parts.length > 2) {
            log.warn("Schema Key {} should be either content_type (e.g. \"json\") of schema_name.content_type (e.g. \"dev.json\")");
            return 0;
        }
        final String configKey = parts.length > 1 ? (parts[0] + ".schemas." + parts[1]) : ("core.schemas." + parts[0]);
        final String schemaId = localization.getConfiguration(configKey);
        try {
            return Integer.parseInt(schemaId);
        } catch (NumberFormatException e) {
            log.warn("Error while parsing schema id: {}", schemaId, e);
            return 0;
        }
    }

    /**
     * Normalizes given path to have an explicit page name and extension.
     * <p>Adds an explicit <code>index</code> page name if other page is not in a <code>path</code>.</p>
     * <p>Adds an explicit <code>.html</code> extension if other extension is not in a <code>path</code>.</p>
     * <pre>
     * <code>&lt;empty&gt;    -> index.html</code>
     * <code>/          -> /index.html</code>
     * <code>test       -> test.html</code>
     * <code>/test/     -> /test/index.html</code>
     * <code>page.ext   -> page.ext</code>
     * </pre>
     *
     * @param path path to normalize
     * @return a normalized path
     */
    public static String normalizePathToDefaults(String path) {
        log.trace("normalizePathToDefaults({})", path);
        String processingPath = path;

        if (isEmpty(processingPath)) {
            return DEFAULT_PAGE_NAME + DEFAULT_PAGE_EXTENSION;
        }

        if (processingPath.endsWith("/")) {
            processingPath = processingPath + DEFAULT_PAGE_NAME + DEFAULT_PAGE_EXTENSION;
        }

        if (!hasExtension(processingPath)) {
            processingPath = processingPath + DEFAULT_PAGE_EXTENSION;
        }

        log.trace("return {}", processingPath);
        return processingPath;
    }

    /**
     * Tries to find a page in localization using the logic from callback.
     *
     * @param path         path a page to find
     * @param localization current localization to find a page in
     * @param callback     logic to find a page
     * @param <T>          way to return the page
     * @return page if found
     * @throws ContentProviderException if page wasn't found
     */
    public static <T> T findPageByPath(String path, Localization localization, TryFindPage<T> callback)
            throws ContentProviderException {
        String processedPath = normalizePathToDefaults(path);
        final int publicationId = Integer.parseInt(localization.getId());

        log.debug("Try to find page: [{}] {}", publicationId, processedPath);

        T page = callback.tryFindPage(processedPath, publicationId);

        if (page == null && !path.endsWith("/") && !hasExtension(path)) {
            processedPath = normalizePathToDefaults(path + '/');
            log.debug("Try to find page (second attempt): [{}] {}", publicationId, processedPath);
            page = callback.tryFindPage(processedPath, publicationId);
        }

        if (page == null) {
            throw new PageNotFoundException("Page not found: [" + publicationId + "] " + processedPath);
        }

        return page;
    }

    /**
     * Strategy interface for providing by caller logic to find a page.
     *
     * @param <T> way to return the page
     */
    public interface TryFindPage<T> {

        /**
         * Tries to find a page by path in publication with given id.
         *
         * @param path          path of a page
         * @param publicationId id of publication
         * @return page if found in a form of T
         * @throws ContentProviderException if got problems resolving a page
         */
        T tryFindPage(String path, int publicationId) throws ContentProviderException;
    }
}

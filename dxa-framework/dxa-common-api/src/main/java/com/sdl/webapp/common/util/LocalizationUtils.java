package com.sdl.webapp.common.util;

import com.sdl.dxa.common.util.PathUtils;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Localization utils class holds helper-methods for the logic related to CM and {@link Localization} operations.
 * @dxa.publicApi
 */
@Slf4j
public final class LocalizationUtils {

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
     * Tries to find a page in localization using the logic from callback.
     *
     * @param path         path a page to find
     * @param localization current localization to find a page in
     * @param callback     logic to find a page
     * @param <T>          way to return the page
     * @return page if found
     * @throws ContentProviderException if page wasn't found
     * @deprecated since 2.0, use R2 Model Service instead
     */
    @Deprecated
    public static <T> T findPageByPath(@NotNull String path, Localization localization, TryFindPage<T> callback)
            throws ContentProviderException {
        String processedPath = PathUtils.normalizePathToDefaults(path);
        final int publicationId = Integer.parseInt(localization.getId());

        log.debug("Try to find page: [{}] {}", publicationId, processedPath);

        T page = callback.tryFindPage(processedPath, publicationId);

        if (page == null && !path.endsWith("/") && !PathUtils.hasExtension(path)) {
            processedPath = PathUtils.normalizePathToDefaults(path + '/');
            log.debug("Try to find page (second attempt): [{}] {}", publicationId, processedPath);
            page = callback.tryFindPage(processedPath, publicationId);
        }

        if (page == null) {
            throw new PageNotFoundException("Page not found: [" + publicationId + "] " + processedPath);
        }

        return page;
    }

    //region Deprecated methods delegated to PathUtils

    /**
     * See {@link PathUtils#normalizePathToDefaults(String)}.
     *
     * @deprecated since 2.0, use {@link PathUtils#normalizePathToDefaults(String)} instead
     */
    @Deprecated
    public static String normalizePathToDefaults(String path) {
        return PathUtils.normalizePathToDefaults(path);
    }

    /**
     * See {@link PathUtils#hasDefaultExtension(String)}}.
     *
     * @deprecated since 2.0, use {@link PathUtils#hasDefaultExtension(String)} instead
     */
    @Deprecated
    public static boolean hasDefaultExtension(@Nullable String path) {
        return PathUtils.hasDefaultExtension(path);
    }

    /**
     * See {@link PathUtils#stripDefaultExtension(String)}}.
     *
     * @deprecated since 2.0, use {@link PathUtils#stripDefaultExtension(String)} instead
     */
    @Deprecated
    @Contract("null -> null; !null -> !null")
    @Nullable
    public static String stripDefaultExtension(@Nullable String path) {
        return PathUtils.stripDefaultExtension(path);
    }

    /**
     * See {@link PathUtils#stripIndexPath(String)}}.
     *
     * @deprecated since 2.0, use {@link PathUtils#stripIndexPath(String)} instead
     */
    @Deprecated
    @Nullable
    public static String stripIndexPath(@Nullable String path) {
        return PathUtils.stripIndexPath(path);
    }

    /**
     * Extracts the current request URL from current request and replaces it with a given path.
     *
     * @param webRequestContext current {@link WebRequestContext}
     * @param newPath           path to replace the old path with
     * @return full URL of current request with given path appended
     * @deprecated since 2.0, use {@link PathUtils#replaceContextPath(String, String)} instead
     */
    @Contract("_, _ -> !null")
    @Deprecated
    public static String replaceRequestContextPath(@NotNull WebRequestContext webRequestContext, @NotNull String newPath) {
        return PathUtils.replaceContextPath(webRequestContext.getBaseUrl(), newPath);
    }

    /**
     * See {@link PathUtils#removeSequenceFromPageTitle(String)}}.
     *
     * @deprecated since 2.0, use {@link PathUtils#removeSequenceFromPageTitle(String)} instead
     */
    @Deprecated
    @Contract("null -> null; !null -> !null")
    public static String removeSequenceFromPageTitle(String pageTitle) {
        return PathUtils.removeSequenceFromPageTitle(pageTitle);
    }

    /**
     * See {@link PathUtils#isWithSequenceDigits(String)}}.
     *
     * @deprecated since 2.0, use {@link PathUtils#isWithSequenceDigits(String)} instead
     */
    @Deprecated
    public static boolean isWithSequenceDigits(String pageTitle) {
        return PathUtils.isWithSequenceDigits(pageTitle);
    }

    /**
     * See {@link PathUtils#isHomePath(String, String)}}.
     *
     * @param localization current localization
     * @deprecated since 2.0, use {@link PathUtils#isHomePath(String, String)} instead
     */
    @Deprecated
    public static boolean isHomePath(@Nullable String urlToCheck, @NonNull Localization localization) {
        return PathUtils.isHomePath(urlToCheck, localization.getPath());
    }

    /**
     * See {@link PathUtils#isIndexPath(String)}}.
     *
     * @deprecated since 2.0, use {@link PathUtils#isIndexPath(String)} instead
     */
    @Deprecated
    public static boolean isIndexPath(@Nullable String urlToCheck) {
        return PathUtils.isIndexPath(urlToCheck);
    }

    /**
     * See {@link PathUtils#isActiveContextPath(String, String, String)}}.
     *
     * @param localization current localization
     * @deprecated since 2.0, use {@link PathUtils#isActiveContextPath(String, String, String)}} instead
     */
    @Deprecated
    public static boolean isActiveContextPath(@Nullable String requestPath, @NonNull Localization localization, @Nullable String path) {
        return PathUtils.isActiveContextPath(requestPath, localization.getPath(), path);
    }
    //endregion

    /**
     * Strategy interface for providing by caller logic to find a page.
     *
     * @param <T> way to return the page
     */
    @FunctionalInterface
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

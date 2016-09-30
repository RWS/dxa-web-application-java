package com.sdl.webapp.common.util;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.sdl.webapp.common.util.FileUtils.hasExtension;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Localization utils class holds helper-methods for the logic related to CM and {@link Localization} operations.
 */
@Slf4j
public final class LocalizationUtils {

    static final String DEFAULT_PAGE_NAME = "index";

    static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static final Pattern PAGE_TITLE_SEQUENCE = Pattern.compile("^(?<sequence>\\d{3}\\s?)(?<pageName>(?<sequenceStop>[^\\d]).*)$");

    private static final Pattern INDEX_PATH_REGEXP = Pattern.compile("^(?<main>.*)(?<index>/(index(\\.html)?)?)$");

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
     * <code>&lt;empty&gt;    -&gt; index.html</code>
     * <code>/          -&gt; /index.html</code>
     * <code>test       -&gt; test.html</code>
     * <code>/test/     -&gt; /test/index.html</code>
     * <code>page.ext   -&gt; page.ext</code>
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
     * Strips the default page extension from the page path.
     *
     * @param path path to process
     * @return path without a default extension
     */
    @Contract("null -> null; !null -> !null")
    @Nullable
    public static String stripDefaultExtension(@Nullable String path) {
        if (path == null) {
            return null;
        }

        if (path.endsWith(DEFAULT_PAGE_EXTENSION)) {
            log.trace("Stripping default extension {} from path {}", DEFAULT_PAGE_EXTENSION, path);
            return path.substring(0, path.lastIndexOf(DEFAULT_PAGE_EXTENSION));
        }
        return path;
    }

    /**
     * Strips the index path from the page path.
     *
     * @param path path to process
     * @return path without 'index' part if any
     */
    @Nullable
    public static String stripIndexPath(@Nullable String path) {
        if (path == null) {
            return null;
        }

        Matcher matcher = INDEX_PATH_REGEXP.matcher(path);
        return matcher.matches() ? defaultIfBlank(matcher.group("main"), "/") : path;
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
     * Extracts the current request URL from current request and replaces it with a given path.
     *
     * @param webRequestContext current {@link WebRequestContext}
     * @param newPath           path to replace the old path with
     * @return full URL of current request with given path appended
     */
    @Contract("_, _ -> !null")
    public static String replaceRequestContextPath(@NotNull WebRequestContext webRequestContext, @NotNull String newPath) {
        return webRequestContext.getBaseUrl().replace(webRequestContext.getRequestPath(), newPath.startsWith("/") ? newPath : ("/" + newPath));
    }

    /**
     * Removes sequence digits from the page title. Sequence number is always 3-digit.
     * If no sequence number is found, then the string is not changed.
     * <pre>
     *     <code>001 Home</code> will be <code>"Home"</code>
     *     <code>Home</code> will stay <code>"Home"</code>
     * </pre>
     *
     * @param pageTitle page title which may contain a sequence number
     * @return string without sequence number, null parameter returns null
     */
    @Contract("null -> null; !null -> !null")
    public static String removeSequenceFromPageTitle(String pageTitle) {
        if (pageTitle == null) {
            return null;
        }

        Matcher matcher = PAGE_TITLE_SEQUENCE.matcher(pageTitle);
        return matcher.matches() ? matcher.group("pageName").replaceFirst("^\\s", "") : pageTitle;
    }

    /**
     * Returns whether the given page title contains sequence numbers.
     *
     * @param pageTitle page title to check
     * @return true if the page contains sequence number, false otherwise
     */
    public static boolean isWithSequenceDigits(String pageTitle) {
        if (pageTitle == null) {
            return false;
        }
        Matcher matcher = PAGE_TITLE_SEQUENCE.matcher(pageTitle);
        return matcher.matches() && matcher.group("sequence") != null;
    }

    /**
     * Tests whether the given path is home (or root) path of the given localization.
     *
     * @param urlToCheck   url to test against
     * @param localization current localization
     * @return whether the URL provided is home (or root)
     */
    public static boolean isHomePath(@Nullable String urlToCheck, @NonNull Localization localization) {
        String homePath = isNullOrEmpty(localization.getPath()) ? "/" : localization.getPath();

        return urlToCheck != null && homePath.equalsIgnoreCase(urlToCheck);
    }

    /**
     * Checks if the given path is an <code>index</code> path. Basically checks if the path ends with either 'index' or 'index.html'.
     * <pre>
     *     /page/index =&gt; true
     *     /page/index.html =&gt; true
     * </pre>
     *
     * @param urlToCheck url to check
     * @return true if index path, false otherwise
     */
    public static boolean isIndexPath(@Nullable String urlToCheck) {
        return urlToCheck != null && INDEX_PATH_REGEXP.matcher(normalizePathToDefaults(urlToCheck)).matches();
    }

    /**
     * Decides if the current request path is in a given context path. In other words decides whether requested {@code path}
     * is in a context of current {@code request}.
     * <pre>
     *     request to {@code /page} is in context of path {@code /page}
     *     request to {@code /page/child} is in context of path {@code /page} and {@code /page/child}
     *            but definitely not in {@code /other} nor {@code /other/child}
     * </pre>
     * <p>There is a special treatment of {@code /} home requests. Home request is only in same context if path is
     * <strong>exactly {@code /}</strong></p> because any request then is in context of home.
     *
     * @param requestPath  current request path
     * @param localization current localization
     * @param path         given path to test against
     * @return whether we can say that request is under context of path
     */
    public static boolean isActiveContextPath(@Nullable String requestPath, @NonNull Localization localization, @Nullable String path) {
        String stripIndexPath = stripIndexPath(path);
        String originatingRequestUri = stripIndexPath(requestPath);
        if (stripIndexPath == null || originatingRequestUri == null) {
            log.trace("Path or originating path is null, return false");
            return false;
        }


        if (isHomePath(originatingRequestUri, localization) || isHomePath(stripIndexPath, localization)) {
            return stripIndexPath.equalsIgnoreCase(originatingRequestUri);
        }

        return originatingRequestUri.startsWith(stripIndexPath);
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

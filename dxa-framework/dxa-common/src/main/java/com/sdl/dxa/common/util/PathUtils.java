package com.sdl.dxa.common.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * This utils class holds helper-methods for the logic related to operations with page paths.
 *
 * @dxa.publicApi
 */
@Slf4j
public final class PathUtils {

    private static final String DEFAULT_PAGE_NAME = "index";

    private static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static final Pattern PAGE_TITLE_SEQUENCE = Pattern.compile("^(?<sequence>\\d{3}\\s?)(?<pageName>(?<sequenceStop>[^\\d]).*)$");

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile(".*?/?(?<fileName>[^/.]*)(\\.(?<extension>[^/.]*))?$");

    private static final Pattern INDEX_PATH_REGEXP = Pattern.compile("^(?<main>.*)(?<index>/(index(\\.html)?)?)$");

    private PathUtils() {
    }

    public static String getDefaultPageName() {
        return DEFAULT_PAGE_NAME;
    }

    public static String getDefaultPageExtension() {
        return DEFAULT_PAGE_EXTENSION;
    }

    /**
     * Replaces multiple slashes from the path.
     * <pre>
     * <code>&lt;empty&gt;    -&gt; /</code>
     * <code>/          -&gt; /</code>
     * <code>//          -&gt; /</code>
     * <code>/articles/ + legacy      -&gt; /articles/legacy</code>
     * <code>/articles + /legacy      -&gt; /articles/legacy</code>
     * <code>/articles/ + /legacy      -&gt; /articles/legacy</code>
     * </pre>
     *
     * @param path path to normalize
     * @return a normalized path
     */
    @Contract("null ,null -> null; _,_ -> !null")
    public static String combinePath(String url, String path) {
        String securedUrl, securedPath;
        if (url == null && path == null) {
            return null;
        }

        securedUrl = url == null ? "" : url;
        securedPath = path == null ? "" : path;

        return securedUrl.concat("/").concat(securedPath).replaceAll("/+", "/");
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
     * Checks if the given path has an extension.
     *
     * @param path path to check
     * @return whether the path has any extension
     */
    public static boolean hasExtension(@NotNull String path) {
        return path.lastIndexOf('.') > path.lastIndexOf('/');
    }

    /**
     * Checks whether the given path ends with a default extension.
     * Keep in mind that page without name is impossible, so passing '.html' would give {@code false}.
     *
     * @param path the path to check
     * @return whether the path ends with the default extension '.html'
     */
    public static boolean hasDefaultExtension(@Nullable String path) {
        return path != null && path.endsWith(DEFAULT_PAGE_EXTENSION) && path.length() > DEFAULT_PAGE_EXTENSION.length();
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
     * Checks if the given path is an <code>index</code> path. Basically checks if the path ends with either 'index' or 'index.html'.
     * Paths thath are finished with "/" and not with "index" are <strong>NOT</strong> index pages. Although they technically are.
     * <pre>
     *     /page/index =&gt; true
     *     /page/index.html =&gt; true
     * </pre>
     *
     * @param urlToCheck url to check
     * @return true if index path, false otherwise
     */
    public static boolean isIndexPath(@Nullable String urlToCheck) {
        return urlToCheck != null && INDEX_PATH_REGEXP.matcher(urlToCheck.replaceFirst("/$", "")).matches();
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
     * @param requestPath      current request path
     * @param localizationPath current localization path
     * @param path             given path to test against
     * @return whether we can say that request is under context of path
     */
    public static boolean isActiveContextPath(@Nullable String requestPath, @Nullable String localizationPath, @Nullable String path) {
        String stripIndexPath = stripIndexPath(path);
        String originatingRequestUri = stripIndexPath(requestPath);
        if (stripIndexPath == null || originatingRequestUri == null) {
            log.trace("Path or originating path is null, return false");
            return false;
        }

        if (isHomePath(originatingRequestUri, localizationPath) || isHomePath(stripIndexPath, localizationPath)) {
            return stripIndexPath.equalsIgnoreCase(originatingRequestUri);
        }

        return originatingRequestUri.startsWith(stripIndexPath);
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
     * Tests whether the given path is home (or root) path of the given localization.
     *
     * @param urlToCheck url to test against
     * @param homePath   path to be considered as home path of the localization
     * @return whether the URL provided is home (or root)
     */
    public static boolean isHomePath(@Nullable String urlToCheck, @Nullable String homePath) {
        return urlToCheck != null && defaultIfEmpty(homePath, "/").equalsIgnoreCase(urlToCheck);
    }

    /**
     * Gets a file name without extension from full filename.
     *
     * @param fullFileName full file name with extension
     * @return file name without extension, or null if doesn't match
     */
    @Contract("null -> null")
    public static String getFileName(@Nullable String fullFileName) {
        return _getFromFileName(fullFileName, "fileName");
    }

    /**
     * Gets an extension without filename from full filename.
     *
     * @param fullFileName full file name with extension
     * @return extension of the file, or null if doesn't match
     */
    @Contract("null -> null")
    public static String getExtension(@Nullable String fullFileName) {
        return _getFromFileName(fullFileName, "extension");
    }

    @Nullable
    @Contract("null, _ -> null")
    private static String _getFromFileName(@Nullable String fullFileName, String groupName) {
        if (fullFileName == null) {
            return null;
        }

        Matcher matcher = FILE_NAME_PATTERN.matcher(fullFileName);
        if (matcher.matches()) {
            return matcher.group(groupName);
        }
        return null;
    }

    /**
     * Replaces the path in a given URL with a given path.
     *
     * @param currentUrl url to change
     * @param newPath    path to replace the old path with
     * @return full URL of current request with given path appended
     */
    @Contract("_, _ -> !null")
    @SneakyThrows(URISyntaxException.class)
    public static String replaceContextPath(@NotNull String currentUrl, @NotNull String newPath) {
        String pathToSet = new URIBuilder(newPath).getPath();
        return new URIBuilder(currentUrl)
                .setPath(pathToSet.startsWith("/") ? pathToSet : ("/" + pathToSet))
                .build().toString();
    }
}

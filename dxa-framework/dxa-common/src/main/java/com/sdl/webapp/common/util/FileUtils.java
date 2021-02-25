package com.sdl.webapp.common.util;

import com.sdl.webapp.common.api.content.ContentProviderException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Utilities to work with files.
 * @dxa.publicApi
 */
@Slf4j
public final class FileUtils {

    protected static final Pattern SYSTEM_FOLDER_PATTERN = Pattern.compile("/system(/v\\d++\\.\\d++)?/[^\\\\]+/.*");

    protected static final String VERSION_JSON = "/version.json";

    private static final String FAVICON_PATH = "/favicon.ico";

    private FileUtils() {
    }

    /**
     * Checks whether file is older that given time (seconds passed in Unix epoch).
     * If file doesn't exist, then it is considered older no matter what timestamp is given.
     *
     * @param file        file to check
     * @param compareTime timestamp to check against
     * @return whether file creation date is older than given timestamp
     */
    public static boolean isFileOlderThan(@NotNull File file, long compareTime) {
        if (log.isTraceEnabled() && !file.exists()) {
            log.trace("File {} does not exist", file.getAbsoluteFile());
        }
        if (log.isDebugEnabled() && !file.canRead()) {
            log.debug("File {} exists, but cannot be read", file.getAbsoluteFile());
        }
        return !file.exists() || !file.canRead() || file.lastModified() < compareTime;
    }

    /**
     * Check if the parent folder structure for this file exists. This can only happen if file itself doesn't yet exist.
     * Creates all needed folder if requested.
     *
     * @param file        file to check
     * @param createIfNot whether to create folder if they are missing
     * @return whether the folder structure does exist <strong>after</strong> method execution
     * may return false if:
     * - parentFile is null - if file is a folder itself
     */
    public static boolean parentFolderExists(@NotNull File file, boolean createIfNot) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            throw new IOException("Cannot create a file in root folder: " + file.getAbsolutePath());
        }
        boolean alreadyExists = parentFile.exists();
        if (!alreadyExists && createIfNot) {
            boolean createdPath = parentFile.mkdirs();
            if (!createdPath) {
                throw new IOException("Cannot create a path to file: " + file.getAbsolutePath());
            }
            alreadyExists = true;
        }
        return alreadyExists;
    }

    /**
     * Checks if the given file needs to be refreshed, and, if file doesn't exist, also checks and created if all the folders structure.
     * Basically performs subsequent calls to {@link #isFileOlderThan(File, long)} and {@link #parentFolderExists(File, boolean)}.
     *
     * @param file file to check
     * @param time time to check
     * @return whether file needs to be refreshed ({@code time} is older than file creation time
     * @throws ContentProviderException if case folders cannot be created
     */
    public static boolean isToBeRefreshed(File file, long time) throws ContentProviderException {
        if (!isFileOlderThan(file, time)) {
            return false;
        }
        try {
            if (!parentFolderExists(file, true)) {
                throw new ContentProviderException("Failed to create parent directory for file: " + file);
            }
        } catch (IOException ex) {
            throw new ContentProviderException("Cannot verify if file " + file.getAbsolutePath() +
                    " is older than " + time, ex);
        }
        //folder exists, file needs to be refreshed
        return true;
    }

    /**
     * Returns if this path is a path of a favicon.
     *
     * @param path the given path to check
     * @return whether it's a favicon
     */
    public static boolean isFavicon(String path) {
        return FAVICON_PATH.equals(path);
    }

    /**
     * Checks whether this path is a part of DXA essential configuration, or some basic files that are required for DXA to start.
     *
     * @param path the given path to check
     * @param localizationPath path of the current localization
     * @return whether DXA need this file to start
     */
    public static boolean isEssentialConfiguration(@NotNull String path, String localizationPath) {
        String processedPath = !"/".equals(localizationPath) && path.startsWith(localizationPath) ? path.replaceFirst(localizationPath, "") : path;
        return VERSION_JSON.equals(processedPath) ||
               SYSTEM_FOLDER_PATTERN.matcher(processedPath).matches() ||
               isFavicon(processedPath);
    }

    /**
     * @deprecated since 2.0, use {@link com.sdl.dxa.common.util.PathUtils#hasExtension(String)} instead
     */
    @Deprecated
    public static boolean hasExtension(@NotNull String path) {
        return path.lastIndexOf('.') > path.lastIndexOf('/');
    }

}

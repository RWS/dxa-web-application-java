package com.sdl.webapp.common.util;

import com.sdl.webapp.common.api.content.ContentProviderException;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Utilities to work with files.
 */
public final class FileUtils {

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
        return !file.exists() || file.lastModified() < compareTime;
    }

    /**
     * Check if the parent folder structure for this file exists. This can only happen if file itself doesn't yet exist.
     * Creates all needed folder if requested.
     *
     * @param file        file to check
     * @param createIfNot whether to create folder if they are missing
     * @return whether the folder structure does exist <strong>after</strong> method execution
     */
    public static boolean parentFolderExists(@NotNull File file, boolean createIfNot) {
        File parentFile = file.getParentFile();

        return parentFile != null && (parentFile.exists() || createIfNot && parentFile.mkdirs());
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
        if (isFileOlderThan(file, time)) {
            if (!parentFolderExists(file, true)) {
                throw new ContentProviderException("Failed to create parent directory for file: " + file);
            }
            return true;
        }
        return false;
    }

    /**
     * @deprecated since 2.0, use {@link com.sdl.dxa.common.util.PathUtils#hasExtension(String)} instead
     */
    @Deprecated
    public static boolean hasExtension(@NotNull String path) {
        return path.lastIndexOf('.') > path.lastIndexOf('/');
    }

}

package com.sdl.webapp.common.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class FileUtils {

    private FileUtils() {
    }

    public static boolean isFileOlderThan(@NotNull File file, long compareTime) {
        return !file.exists() || file.lastModified() < compareTime;
    }

    public static boolean parentFolderExists(@NotNull File file, boolean createIfNot) {
        File parentFile = file.getParentFile();

        return parentFile != null && (parentFile.exists() || createIfNot && parentFile.mkdirs());
    }

    public static boolean hasExtension(@NotNull String path) {
        return path.lastIndexOf('.') > path.lastIndexOf('/');
    }

}

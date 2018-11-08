package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
public class DefaultBinaryProvider {
    private static final String STATIC_FILES_DIR = "BinaryData";

    protected WebApplicationContext webApplicationContext;

    public DefaultBinaryProvider(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    public StaticContentItem getStaticContent(ContentProvider provider, int binaryId, String localizationId, String localizationPath)
            throws ContentProviderException {
        Path pathToBinaries = getPathToBinaryFiles(localizationId);
        String[] files = getFiles(binaryId, localizationId, pathToBinaries);
        return processBinaryFile(provider, binaryId, localizationId, localizationPath, files);
    }

    @NotNull
    FilenameFilter getFilenameFilter(int binaryId, String localizationId) {
        return (path, name) -> name.matches(".*_tcm" + localizationId + "-" + binaryId + "\\D.*");
    }

    @NotNull
    Path getPathToBinaryFiles(String localizationId) {
        String parentPath = StringUtils.join(new String[]{getBasePath(), STATIC_FILES_DIR, localizationId, "media"}, File.separator);
        return Paths.get(parentPath);
    }

    StaticContentItem processBinaryFile(ContentProvider provider, int binaryId, String localizationId, String localizationPath, String[] files) throws ContentProviderException {
        if (files == null || files.length <= 0) {
            log.warn("There are no binary files by Id " + binaryId + " for localizationId " + localizationId);
            return null;
        }
        if (files.length > 1) {
            log.warn("There are more than 1 file for binaryId " + binaryId + " for localizationId " + localizationId + " {"+ Arrays.toString(files) + "}");
        }
        return provider.getStaticContent(files[0], localizationId, localizationPath);
    }

    @NotNull
    String getBasePath() {
        String basePath = getAppRealPath();
        if (basePath.endsWith(File.separator)) {
            basePath = basePath.substring(0, basePath.length()-1);
        }
        return basePath;
    }

    String[] getFiles(int binaryId, String localizationId, Path pathToBinaries) {
        return pathToBinaries.toFile().list(getFilenameFilter(binaryId, localizationId));
    }

    String getAppRealPath() {
        return webApplicationContext.getServletContext().getRealPath("/");
    }
}

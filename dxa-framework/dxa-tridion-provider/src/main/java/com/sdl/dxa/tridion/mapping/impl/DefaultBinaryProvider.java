package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        String[] files = pathToBinaries.toFile().list((path, name) -> name.matches(".*_tcm" + localizationId + "-" + binaryId + "\\D.*"));
        return processBinaryFile(provider, localizationId, localizationPath, files);
    }

    @NotNull
    private Path getPathToBinaryFiles(String localizationId) {
        String parentPath = StringUtils.join(new String[]{getBasePath(), STATIC_FILES_DIR, localizationId, "media"}, File.separator);
        return Paths.get(parentPath);
    }

    private StaticContentItem processBinaryFile(ContentProvider provider, String localizationId, String localizationPath, String[] files) throws ContentProviderException {
        if (files == null || files.length <= 0) {
            return null;
        }
        return provider.getStaticContent(files[0], localizationId, localizationPath));
    }

    @NotNull
    private String getBasePath() {
        String basePath = webApplicationContext.getServletContext().getRealPath("/");
        if (basePath.endsWith(File.separator)) basePath = basePath.substring(0, basePath.length()-1);
        return basePath;
    }
}

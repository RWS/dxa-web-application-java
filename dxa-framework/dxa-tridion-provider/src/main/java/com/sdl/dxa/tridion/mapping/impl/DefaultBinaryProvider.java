package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Slf4j
public class DefaultBinaryProvider {
    private static final String STATIC_FILES_DIR = "BinaryData";

    protected WebApplicationContext webApplicationContext;

    public DefaultBinaryProvider(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    public StaticContentItem getStaticContent(ContentProvider provider, int binaryId, String localizationId, String localizationPath) throws ContentProviderException {
        String basePath = webApplicationContext.getServletContext().getRealPath("/");
        if (basePath.endsWith(File.separator)) basePath = basePath.substring(0, basePath.length()-1);
        String parentPath = StringUtils.join(new String[]{basePath, STATIC_FILES_DIR, localizationId, "media"}, File.separator);
        Path pathToBinaries = Paths.get(parentPath.trim());
        AtomicReference<StaticContentItem> result = new AtomicReference<>();
        AtomicReference<ContentProviderException> exception = new AtomicReference<>();
        String[] files = pathToBinaries.toFile().list((path, name) -> name.matches(".*_tcm" + localizationId + "-" + binaryId + "\\D.*"));
        if (files != null && files.length > 0) {
            String path = files[0];
            try {
                result.set(provider.getStaticContent(path, localizationId, localizationPath));
            } catch (ContentProviderException e) {
                exception.set(new ContentProviderException("Binary content cannot be loaded by path " + path, e));
            }
        }
        if (exception.get() != null) throw exception.get();
        if (result.get() == null) {
            log.info("Binary content for publication " + localizationId + " and id " + binaryId + " is not found");
        }
        return result.get();
    }
}

package com.sdl.dxa.tridion.mapping.impl;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.rometools.utils.Lists;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.BinaryComponent;
import com.sdl.web.pca.client.contentmodel.generated.BinaryVariant;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
public class GraphQLBinaryContentProvider {
    private static final String STATIC_FILES_DIR = "BinaryData";

    protected WebApplicationContext webApplicationContext;

    protected ApiClient pcaClient;

    public GraphQLBinaryContentProvider(ApiClient pcaClient, WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
        this.pcaClient = pcaClient;
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

    StaticContentItem processBinaryFile(ContentProvider provider,
                                        int binaryId,
                                        String localizationId,
                                        String localizationPath,
                                        String[] files) throws ContentProviderException {
        if (files == null || files.length <= 0) {
            return downloadBinary(provider, binaryId, localizationId, localizationPath);
        }
        if (files.length > 1) {
            log.warn("There are more than 1 file with binaryId: " + binaryId + " for localizationId: " + localizationId + " {"+ Arrays.toString(files) + "}");
        }
        return provider.getStaticContent(files[0], localizationId, localizationPath);
    }

    @Nullable
    StaticContentItem downloadBinary(ContentProvider provider,
                                     int binaryId,
                                     String localizationId,
                                     String localizationPath) throws ContentProviderException {
        log.debug("There is no binary file with binaryId: " + binaryId + " for localizationId: " + localizationId);
        BinaryComponent binaryComponent = pcaClient.getBinaryComponent(ContentNamespace.Sites, Ints.tryParse(localizationId), binaryId, null, null);
        if (binaryComponent == null) {
            throw new DxaItemNotFoundException("There is no binary with binaryId: " + binaryId + " for publication: " + localizationId);
        }
        try {
            if (binaryComponent.getVariants() == null) {
                log.error("Unable to get binary data (Variants null) for binary component with id: " + binaryComponent.getId());
                return null;
            }
            if (Lists.isEmpty(binaryComponent.getVariants().getEdges())) {
                log.error("Unable to get binary data (Edges null) for binary component with id: " + binaryComponent.getId());
                return null;
            }
            BinaryVariant variant = binaryComponent.getVariants().getEdges().get(0).getNode();
            if (Strings.isNullOrEmpty(variant.getDownloadUrl())) {
                log.error("Binary variant download Url is missing for binary component with id: " + binaryComponent.getId());
                return null;
            }
            log.debug("Attempting to get binary content for " + variant.getDownloadUrl() + " from binary component with id: " + binaryComponent.getId());
            return provider.getStaticContent(variant.getPath(), localizationId, localizationPath);
        }
        catch(Exception ex) {
            throw new ContentProviderException("Could not get binary file with binaryId: " + binaryId + " for localizationId: " + localizationId, ex);
        }
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

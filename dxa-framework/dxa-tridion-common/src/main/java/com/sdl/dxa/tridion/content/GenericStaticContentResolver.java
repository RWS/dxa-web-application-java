package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.webapp.common.impl.model.ContentNamespace;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import com.sdl.webapp.common.util.ImageUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Slf4j
public abstract class GenericStaticContentResolver implements StaticContentResolver {

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");
    private static final String STATIC_FILES_DIR = "BinaryData";
    protected static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    protected WebApplicationContext webApplicationContext;

    @Override
    public @NotNull StaticContentItem getStaticContent(@NotNull StaticContentRequestDto requestDto) throws ContentProviderException {
        return getStaticContent(ContentNamespace.Sites, requestDto);
    }

    FilenameFilter getFilenameFilter(ContentNamespace contentNamespace, int binaryId, String localizationId) {
        String nameSpace = contentNamespace == ContentNamespace.Sites
                ? ContentNamespace.Sites.nameSpace()
                : ContentNamespace.Docs.nameSpace();
        return (path, name) -> name.matches(".*_" + nameSpace + localizationId + "-" + binaryId + "\\D.*");
    }

    @NotNull
    Path getPathToBinaryFiles(String localizationId) {
        String parentPath = StringUtils.join(new String[]{getBasePath(), STATIC_FILES_DIR, localizationId, "media"}, File.separator);
        return Paths.get(parentPath);
    }

    @NotNull
    String getBasePath() {
        String basePath = getAppRealPath();
        if (basePath.endsWith(File.separator)) {
            basePath = basePath.substring(0, basePath.length()-1);
        }
        return basePath;
    }

    String[] getFiles(ContentNamespace contentNamespace, int binaryId, String localizationId, Path pathToBinaries) {
        return pathToBinaries.toFile().list(getFilenameFilter(contentNamespace, binaryId, localizationId));
    }

    String getAppRealPath() {
        return webApplicationContext.getServletContext().getRealPath("/");
    }

    public @NotNull StaticContentItem getStaticContent(ContentNamespace namespace, @NotNull StaticContentRequestDto requestDto) throws ContentProviderException {
        log.trace("getStaticContent: {}", requestDto);

        StaticContentRequestDto adaptedRequest = requestDto.isLocalizationPathSet()
                ? requestDto
                : requestDto.toBuilder().localizationPath(resolveLocalizationPath(requestDto, namespace)).build();

        if(requestDto.getBinaryPath() != null) {
            final String contentPath = getContentPath(adaptedRequest.getBinaryPath(), adaptedRequest.getLocalizationPath());

            return getStaticContentFileByPath(namespace, contentPath, adaptedRequest);
        } else {
            return getStaticContentItemById(namespace, requestDto.getBinaryId(), adaptedRequest);
        }
    }

    private String getContentPath(@NotNull String binaryPath, @NotNull String localizationPath) {
        if (localizationPath.length() > 1) {
            String path = binaryPath.startsWith(localizationPath) ? binaryPath.substring(localizationPath.length()) : binaryPath;
            return localizationPath + removeVersionNumber(path);
        }
        return removeVersionNumber(binaryPath);
    }

    private String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    protected @NotNull String getPublicationPath(String publicationId) {
        return StringUtils.join(new String[]{ webApplicationContext.getServletContext().getRealPath("/"), STATIC_FILES_DIR, publicationId }, File.separator);
    }

    private @NotNull StaticContentItem getStaticContentFileByPath(ContentNamespace namespace, String path, StaticContentRequestDto requestDto) throws ContentProviderException {
        String parentPath = getPublicationPath(requestDto.getLocalizationId());

        final File file = new File(parentPath, path);
        log.trace("getStaticContentFileByPath: {}", file.getAbsolutePath());

        final ImageUtils.StaticContentPathInfo pathInfo = new ImageUtils.StaticContentPathInfo(path);

        int publicationId = Integer.parseInt(requestDto.getLocalizationId());

        String urlPath = prependFullUrlIfNeeded(pathInfo.getFileName(), requestDto.getBaseUrl());

        return createStaticContentItem(namespace, requestDto, file, publicationId, pathInfo, urlPath);

    }

    @SneakyThrows(UnsupportedEncodingException.class)
    protected String prependFullUrlIfNeeded(String path, String baseUrl) {
        if (path.contains(baseUrl)) {
            return path;
        }
        return UriUtils.encodePath(baseUrl + path, "UTF-8");
    }

    protected void refreshBinary(File file, ImageUtils.StaticContentPathInfo pathInfo, byte[] binaryContent) throws ContentProviderException {
        log.debug("Writing binary content to file: {}", file);
        try {
            ImageUtils.writeToFile(file, pathInfo, binaryContent);
        } catch (IOException e) {
            throw new StaticContentNotLoadedException("Cannot write new loaded content to a file " + file.getAbsolutePath(), e);
        }
    }

    @NotNull
    protected abstract StaticContentItem createStaticContentItem(
            ContentNamespace namespace,
            StaticContentRequestDto requestDto,
            File file,
            int publicationId,
            ImageUtils.StaticContentPathInfo pathInfo,
            String urlPath
    ) throws ContentProviderException;

    protected abstract @NotNull StaticContentItem getStaticContentItemById(ContentNamespace namespace, int binaryId, StaticContentRequestDto requestDto) throws ContentProviderException;

    protected abstract String resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException;

    protected abstract String resolveLocalizationPath(StaticContentRequestDto requestDto, ContentNamespace namespace) throws StaticContentNotLoadedException;

}

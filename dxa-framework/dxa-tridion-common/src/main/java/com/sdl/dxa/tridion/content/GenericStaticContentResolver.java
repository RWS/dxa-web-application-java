package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

@Slf4j
public abstract class GenericStaticContentResolver implements StaticContentResolver {

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");
    private static final String STATIC_FILES_DIR = "BinaryData";
    protected static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    protected WebApplicationContext webApplicationContext;

    @Override
    public @NotNull StaticContentItem getStaticContent(@NotNull StaticContentRequestDto requestDto) throws ContentProviderException {
        log.trace("getStaticContent: {}", requestDto);

        StaticContentRequestDto adaptedRequest = requestDto.isLocalizationPathSet() ? requestDto :
                requestDto.toBuilder().localizationPath(
                        _resolveLocalizationPath(requestDto)).build();

        final String contentPath = _getContentPath(adaptedRequest.getBinaryPath(), adaptedRequest.getLocalizationPath());

        return _getStaticContentFile(contentPath, adaptedRequest);
    }

    private String _getContentPath(@NotNull String binaryPath, @NotNull String localizationPath) {
        if (localizationPath.length() > 1) {
            String path = binaryPath.startsWith(localizationPath) ? binaryPath.substring(localizationPath.length()) : binaryPath;
            return localizationPath + removeVersionNumber(path);
        } else {
            return removeVersionNumber(binaryPath);
        }
    }

    private String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    private StaticContentItem _getStaticContentFile(String path, StaticContentRequestDto requestDto)
            throws ContentProviderException {
        String parentPath = StringUtils.join(new String[]{
                webApplicationContext.getServletContext().getRealPath("/"), STATIC_FILES_DIR, requestDto.getLocalizationId()
        }, File.separator);

        final File file = new File(parentPath, path);
        log.trace("getStaticContentFile: {}", file);

        final ImageUtils.StaticContentPathInfo pathInfo = new ImageUtils.StaticContentPathInfo(path);

        int publicationId = Integer.parseInt(requestDto.getLocalizationId());

        String urlPath = _prependFullUrlIfNeeded(pathInfo.getFileName(), requestDto.getBaseUrl());

        return createStaticContentItem(requestDto, file, publicationId, pathInfo, urlPath);
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    private String _prependFullUrlIfNeeded(String path, String baseUrl) {
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
            throw new StaticContentNotLoadedException("Cannot write new loaded content to a file " + file, e);
        }
    }

    @NotNull
    protected abstract StaticContentItem createStaticContentItem(final StaticContentRequestDto requestDto,
                                                      final File file,
                                                      int publicationId,
                                                      ImageUtils.StaticContentPathInfo pathInfo,
                                                      String urlPath) throws ContentProviderException;

    protected abstract String _resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException;
}

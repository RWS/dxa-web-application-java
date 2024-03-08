package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import com.sdl.webapp.common.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static com.sdl.webapp.common.util.FileUtils.parentFolderExists;

@Slf4j
public abstract class GenericStaticContentResolver implements StaticContentResolver {

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d++\\.\\d++/");
    private static final String STATIC_FILES_DIR = "BinaryData";
    protected static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    protected WebApplicationContext webApplicationContext;

    @Override
    @NotNull
    public StaticContentItem getStaticContent(@NotNull StaticContentRequestDto requestDto) throws ContentProviderException {
        log.trace("getStaticContent: {}", requestDto);
        StaticContentRequestDto request = requestDto.isLocalizationPathSet()
                ? requestDto
                : requestDto.toBuilder().localizationPath(resolveLocalizationPath(requestDto)).build();

        if (requestDto.getBinaryPath() != null) {
            String contentPath = getContentPath(request.getBinaryPath(), request.getLocalizationPath());
            return getStaticContentFileByPath(contentPath, request);
        }
        return getStaticContentItemById(requestDto.getBinaryId(), request);
    }

    @NotNull
    String getContentPath(@NotNull String binaryPath, @NotNull String localizationPath) {
        if (localizationPath.length() > 1) {
            String path = binaryPath.startsWith(localizationPath + "/")
                    ? binaryPath.substring(localizationPath.length())
                    : binaryPath;
            return localizationPath + removeVersionNumber(path);
        }
        return removeVersionNumber(binaryPath);
    }

    @NotNull
    String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    protected @NotNull String getPublicationPath(String publicationId) {
        return StringUtils.join(new String[]{
                getRealPath(),
                STATIC_FILES_DIR,
                publicationId
        }, File.separator);
    }

    String getRealPath() {
        return webApplicationContext.getServletContext().getRealPath("/");
    }

    @NotNull
    private StaticContentItem getStaticContentFileByPath(String path, StaticContentRequestDto requestDto) throws ContentProviderException {
        final Path parentPath = Paths.get(getPublicationPath(requestDto.getLocalizationId())).normalize();
        final Path parentRelativePath = parentPath.subpath(0, parentPath.getNameCount() - 1);
        final Path normalized_path = Paths.get(path).normalize();

        final File file = getFile(parentPath, normalized_path, parentRelativePath);
        log.trace("getStaticContentFileByPath: {}", file.getAbsolutePath());

        final ImageUtils.StaticContentPathInfo pathInfo = new ImageUtils.StaticContentPathInfo(path);
        int publicationId = Integer.parseInt(requestDto.getLocalizationId());
        String urlPath = prependFullUrlIfNeeded(pathInfo.getFileName(), requestDto.getBaseUrl());
        return createStaticContentItem(requestDto, file, publicationId, pathInfo, urlPath);
    }

    @NotNull
    private static File getFile(Path parentPath, Path normalized_path, Path parentRelativePath) throws ContentProviderException {
        final File file = new File(parentPath.toString(), normalized_path.toString());
        final Path filePath = file.toPath();
        final Path fileRelativePath = filePath.subpath(0, filePath.getNameCount()-1);

        try {
            if (!fileRelativePath.startsWith(parentRelativePath)) {
                throw new ContentProviderException("The path to the static file not starting with the expected " +
                        "parent path. [" + file.getCanonicalPath() + "]");
            }
        }
        catch(IOException ioException) {
            throw new ContentProviderException(ioException);
        }
        return file;
    }

    protected String prependFullUrlIfNeeded(String path, String baseUrl) {
        if (path.contains(baseUrl)) {
            return path;
        }
        return UriUtils.encodePath(baseUrl + path, "UTF-8");
    }

    /**
     * Note: this code is not a thread-safe so it should be called inside sync block.
     * @param file to be refreshed.
     * @param pathInfo path to a binary.
     * @param binaryContent binary file content to be written.
     * @throws ContentProviderException is thrown if saving cannot be completed.
     */
    protected void refreshBinary(File file, ImageUtils.StaticContentPathInfo pathInfo, byte[] binaryContent)
            throws ContentProviderException {
        log.debug("Writing binary content to file: {}", file);
        try {
            if (!parentFolderExists(file, true)) {
                throw new ContentProviderException("Failed to create parent directory for file: " + file);
            }
            if (log.isWarnEnabled() && file.exists() && !file.canWrite()) {
                log.warn("File {} exists and cannot be written", file);
            }
            ImageUtils.writeToFile(file, pathInfo, binaryContent);
        } catch (IOException e) {
            throw new StaticContentNotLoadedException("Cannot write new loaded content to a file: " + file.getAbsolutePath(), e);
        }
    }

    @NotNull
    protected abstract StaticContentItem createStaticContentItem(
            StaticContentRequestDto requestDto,
            File file,
            int publicationId,
            ImageUtils.StaticContentPathInfo pathInfo,
            String urlPath
    ) throws ContentProviderException;

    protected abstract @NotNull StaticContentItem getStaticContentItemById(int binaryId, StaticContentRequestDto requestDto) throws ContentProviderException;

    protected abstract String resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException;
}

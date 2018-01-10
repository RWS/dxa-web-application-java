package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.web.api.meta.WebComponentMetaFactory;
import com.sdl.web.api.meta.WebComponentMetaFactoryImpl;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import com.sdl.webapp.common.util.ImageUtils;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.broker.StorageException;
import com.tridion.content.BinaryFactory;
import com.tridion.data.BinaryData;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.PublicationMeta;
import com.tridion.meta.PublicationMetaFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import static com.sdl.webapp.common.util.FileUtils.isToBeRefreshed;

/**
 * Static content resolver is capable to resolve static (also versioned) binary content from broker database, and to cache it for same request.
 *
 * @dxa.publicApi
 */
@Slf4j
@Service
public class StaticContentResolver {

    private static final Object LOCK = new Object();

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");

    private static final String STATIC_FILES_DIR = "BinaryData";

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final WebApplicationContext webApplicationContext;

    private final DynamicMetaRetriever dynamicMetaRetriever = new DynamicMetaRetriever();

    private final BinaryFactory binaryContentRetriever = new BinaryFactory();

    private final PublicationMetaFactory publicationMetaFactory = new PublicationMetaFactory();

    @Autowired
    public StaticContentResolver(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    /**
     * Resolves static content with a given path in a given publication.
     * <p>Requires localization path to request the content, so resolves it using localization ID if the path is missing.
     * If you already know publication path, providing it in a request would give you a bit better performance. </p>
     * If file is resolved, caches the file locally, so won't download it again unless it needs to be refreshed.
     *
     * @param requestDto request DTO
     * @return requested static file
     * @throws StaticContentNotFoundException if cannot resolve static file for any reason
     * @dxa.publicApi
     */
    @NotNull
    public StaticContentItem getStaticContent(@NotNull StaticContentRequestDto requestDto) throws ContentProviderException {
        log.trace("getStaticContent: {}", requestDto);

        StaticContentRequestDto adaptedRequest = requestDto.isLocalizationPathSet() ? requestDto :
                requestDto.toBuilder().localizationPath(
                        _resolveLocalizationPath(requestDto.getLocalizationId())).build();

        final String contentPath = _getContentPath(adaptedRequest.getBinaryPath(), adaptedRequest.getLocalizationPath());

        return _getStaticContentFile(contentPath, adaptedRequest);
    }

    private String _resolveLocalizationPath(String localizationId) throws StaticContentNotLoadedException {
        PublicationMeta meta;
        try {
            meta = publicationMetaFactory.getMeta(TcmUtils.buildPublicationTcmUri(localizationId));
        } catch (StorageException e) {
            throw new StaticContentNotLoadedException("Cannot resolve localization path for localization '" + localizationId + "'", e);
        }
        log.debug("Resolved url '{}' for publication id {}", meta.getPublicationPath(), localizationId);
        return meta.getPublicationUrl();
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
        BinaryMeta binaryMeta;
        WebComponentMetaFactory factory = new WebComponentMetaFactoryImpl(publicationId);
        ComponentMeta componentMeta;
        int itemId;

        synchronized (LOCK) {
            binaryMeta = dynamicMetaRetriever.getBinaryMetaByURL(_prependFullUrlIfNeeded(pathInfo.getFileName(), requestDto.getBaseUrl()));
            if (binaryMeta == null) {
                throw new StaticContentNotFoundException("No binary meta found for: [" + publicationId + "] " +
                        pathInfo.getFileName());
            }
            itemId = (int) binaryMeta.getURI().getItemId();
            componentMeta = factory.getMeta(itemId);
            if (componentMeta == null) {
                throw new StaticContentNotFoundException("No meta meta found for: [" + publicationId + "] " +
                        pathInfo.getFileName());
            }
        }

        long componentTime = componentMeta.getLastPublicationDate().getTime();

        boolean shouldRefresh = isToBeRefreshed(file, componentTime) || requestDto.isNoMediaCache();

        if (shouldRefresh) {
            BinaryData binaryData = binaryContentRetriever.getBinary(publicationId, itemId, binaryMeta.getVariantId());

            log.debug("Writing binary content to file: {}", file);
            try {
                ImageUtils.writeToFile(file, pathInfo, binaryData.getBytes());
            } catch (IOException e) {
                throw new StaticContentNotLoadedException("Cannot write new loaded content to a file " + file, e);
            }
        } else {
            log.debug("File does not need to be refreshed: {}", file);
        }

        return new StaticContentItem() {
            @Override
            public long getLastModified() {
                return file.lastModified();
            }

            @Override
            public String getContentType() {
                return StringUtils.isEmpty(binaryMeta.getType()) ? DEFAULT_CONTENT_TYPE : binaryMeta.getType();
            }

            @Override
            public InputStream getContent() throws IOException {
                return new BufferedInputStream(new FileInputStream(file));
            }

            @Override
            public boolean isVersioned() {
                return requestDto.getBinaryPath().contains("/system/");
            }
        };
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    private String _prependFullUrlIfNeeded(String path, String baseUrl) {
        if (path.contains(baseUrl)) {
            return path;
        }
        return UriUtils.encodePath(baseUrl + path, "UTF-8");
    }
}

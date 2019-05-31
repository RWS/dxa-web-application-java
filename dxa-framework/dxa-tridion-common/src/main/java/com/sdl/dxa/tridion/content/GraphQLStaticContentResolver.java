package com.sdl.dxa.tridion.content;

import com.google.common.primitives.Ints;
import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.generated.BinaryComponent;
import com.sdl.web.pca.client.contentmodel.generated.BinaryVariant;
import com.sdl.web.pca.client.contentmodel.generated.Publication;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import com.sdl.webapp.common.impl.model.ContentNamespace;
import com.sdl.webapp.common.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;

import static com.sdl.dxa.tridion.common.ContextDataCreator.createContextData;
import static com.sdl.webapp.common.util.FileUtils.isToBeRefreshed;

@Slf4j
@Service("graphQLStaticContentResolver")
@Profile("!cil.providers.active")
public class GraphQLStaticContentResolver extends GenericStaticContentResolver implements StaticContentResolver {

    private static final Object LOCK = new Object();

    private ApiClient apiClient;

    private BinaryContentDownloader contentDownloader;

    @Autowired
    public GraphQLStaticContentResolver(WebApplicationContext webApplicationContext,
                                        ApiClientProvider apiClientProvider,
                                        BinaryContentDownloader contentDownloader) {
        this.apiClient = apiClientProvider.getClient();
        this.contentDownloader = contentDownloader;
        this.webApplicationContext = webApplicationContext;
    }

    @NotNull
    protected StaticContentItem createStaticContentItem(
            StaticContentRequestDto requestDto,
            File file,
            int publicationId,
            ImageUtils.StaticContentPathInfo pathInfo,
            String urlPath) throws ContentProviderException {
        return createStaticContentItem(ContentNamespace.Sites, requestDto, file, publicationId, pathInfo, urlPath);
    }

    protected StaticContentItem createStaticContentItem(
            ContentNamespace namespace,
            StaticContentRequestDto requestDto,
            File file,
            int publicationId,
            ImageUtils.StaticContentPathInfo pathInfo,
            String urlPath) throws ContentProviderException {
        BinaryComponent binaryComponent = apiClient.getBinaryComponent(
                convertNamespace(namespace),
                publicationId,
                pathInfo.getFileName(),
                "",
                createContextData(requestDto.getClaims()));

        return this.processBinaryComponent(binaryComponent, requestDto, file, urlPath, pathInfo);
    }

    @Override
    protected @NotNull StaticContentItem getStaticContentItemById(ContentNamespace namespace, int binaryId, StaticContentRequestDto requestDto) throws ContentProviderException {
        BinaryComponent binaryComponent = apiClient.getBinaryComponent(
                convertNamespace(namespace),
                Ints.tryParse(requestDto.getLocalizationId()),
                binaryId,
                null,
                null);

        String parentPath = getPublicationPath(requestDto.getLocalizationId());
        String path = binaryComponent.getVariants().getEdges().get(0).getNode().getPath();

        final File file = new File(parentPath, path);
        final ImageUtils.StaticContentPathInfo pathInfo = new ImageUtils.StaticContentPathInfo(path);

        String urlPath = prependFullUrlIfNeeded(pathInfo.getFileName(), requestDto.getBaseUrl());

        return this.processBinaryComponent(binaryComponent, requestDto, file, urlPath, pathInfo);
    }

    private void refreshBinary(File file, ImageUtils.StaticContentPathInfo pathInfo, BinaryComponent binaryComponent) throws ContentProviderException {
        String downloadUrl = binaryComponent.getVariants().getEdges().get(0).getNode().getDownloadUrl();

        byte[] content = contentDownloader.downloadContent(file, downloadUrl);

        refreshBinary(file, pathInfo, content);
    }

    public String resolveLocalizationPath(StaticContentRequestDto requestDto, ContentNamespace namespace) throws StaticContentNotLoadedException {
        int publicationId = Integer.parseInt(requestDto.getLocalizationId());
        ContextData contextData = createContextData(requestDto.getClaims());
        Publication publication = apiClient.getPublication(
                convertNamespace(namespace),
                publicationId,
                "",
                contextData);
        return publication.getPublicationUrl();

    };

    @Override
    protected String resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException {
        return resolveLocalizationPath(requestDto, com.sdl.webapp.common.impl.model.ContentNamespace.Sites);
    }

    private com.sdl.web.pca.client.contentmodel.enums.ContentNamespace convertNamespace(ContentNamespace namespace) {
        return namespace.equals(ContentNamespace.Sites) ? com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Sites : com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Docs;
    }

    private boolean isVersioned(@NotNull String path) {
        return path.contains("/system/");
    }

    private StaticContentItem processBinaryComponent(BinaryComponent binaryComponent, StaticContentRequestDto requestDto, File file, String urlPath, ImageUtils.StaticContentPathInfo pathInfo) throws ContentProviderException {
        if (binaryComponent == null) {
            throw new StaticContentNotFoundException("No binary found for pubId: [" +
                    requestDto.getLocalizationId() + "] and urlPath: " + urlPath);
        }

        long componentTime = new DateTime(binaryComponent.getLastPublishDate()).getMillis();

        boolean shouldRefreshed = isToBeRefreshed(file, componentTime) || requestDto.isNoMediaCache();

        if (shouldRefreshed) {
            log.debug("File needs to be refreshed: {}", file.getAbsolutePath());
            synchronized (LOCK) {
                refreshBinary(file, pathInfo, binaryComponent);
            }
        } else {
            log.debug("File does not need to be refreshed: {}", file.getAbsolutePath());
        }

        BinaryVariant variant = binaryComponent.getVariants().getEdges().get(0).getNode();
        String binaryComponentType = variant.getType();
        String contentType = StringUtils.isEmpty(binaryComponentType) ? DEFAULT_CONTENT_TYPE : binaryComponentType;
        boolean versioned = isVersioned(variant.getPath());
        return new StaticContentItem(contentType, file, versioned);
    }
}

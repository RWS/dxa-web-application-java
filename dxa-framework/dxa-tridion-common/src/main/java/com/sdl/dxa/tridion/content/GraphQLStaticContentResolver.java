package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.BinaryComponent;
import com.sdl.web.pca.client.contentmodel.generated.Publication;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
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
    public GraphQLStaticContentResolver(WebApplicationContext webApplicationContext, ApiClientProvider apiClientProvider,
                                        BinaryContentDownloader contentDownloader) {
        this.apiClient = apiClientProvider.getClient();
        this.contentDownloader = contentDownloader;
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    @NotNull
    protected StaticContentItem createStaticContentItem(StaticContentRequestDto requestDto, File file,
                                                        int publicationId, ImageUtils.StaticContentPathInfo pathInfo,
                                                        String urlPath) throws ContentProviderException {
        BinaryComponent binaryComponent = apiClient.getBinaryComponent(ContentNamespace.Sites,
                    publicationId,
                    pathInfo.getFileName(),
                    "",
                    createContextData(requestDto.getClaims()));

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
        String binaryComponentType = binaryComponent.getVariants().getEdges().get(0).getNode().getType();
        String contentType = StringUtils.isEmpty(binaryComponentType) ? DEFAULT_CONTENT_TYPE : binaryComponentType;
        boolean versioned = requestDto.getBinaryPath().contains("/system/");
        return new StaticContentItem(contentType, file, versioned);
    }

    private void refreshBinary(File file, ImageUtils.StaticContentPathInfo pathInfo, BinaryComponent binaryComponent) throws ContentProviderException {
        String downloadUrl = binaryComponent.getVariants().getEdges().get(0).getNode().getDownloadUrl();

        byte[] content = contentDownloader.downloadContent(file, downloadUrl);

        refreshBinary(file, pathInfo, content);
    }

    @Override
    protected String resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException {
        int publicationId = Integer.parseInt(requestDto.getLocalizationId());
        ContextData contextData = createContextData(requestDto.getClaims());
        Publication publication = apiClient.getPublication(ContentNamespace.Sites,
                publicationId,
                "",
                contextData);
        return publication.getPublicationUrl();
    }
}

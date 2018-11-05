package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
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
@Service
@Profile("!cil.providers.active")
public class GraphQLStaticContentResolver extends GenericStaticContentResolver implements StaticContentResolver {

    private static final Object LOCK = new Object();

    @Autowired
    private ApiClientProvider pcaClientProvider;

    @Autowired
    private BinaryContentDownloader contentDownloader;

    @Autowired
    public GraphQLStaticContentResolver(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    @NotNull
    protected StaticContentItem createStaticContentItem(StaticContentRequestDto requestDto, File file,
                                                        int publicationId, ImageUtils.StaticContentPathInfo pathInfo,
                                                        String urlPath) throws ContentProviderException {
        BinaryComponent binaryComponent;

        synchronized (LOCK) {
            binaryComponent = pcaClientProvider.getClient().getBinaryComponent(ContentNamespace.Sites,
                    publicationId,
                    pathInfo.getFileName(),
                    "",
                    createContextData(requestDto.getClaims()));

            long componentTime = new DateTime(binaryComponent.getLastPublishDate()).getMillis();

            boolean shouldRefresh = isToBeRefreshed(file, componentTime) || requestDto.isNoMediaCache();

            if (shouldRefresh) {
                refreshBinary(file, pathInfo, binaryComponent);
            } else {
                log.debug("File does not need to be refreshed: {}", file);
            }
        }

        String binaryComponentType = binaryComponent.getVariants().getEdges().get(0).getNode().getType();
        String contentType = StringUtils.isEmpty(binaryComponentType) ? DEFAULT_CONTENT_TYPE : binaryComponentType;
        boolean versioned = requestDto.getBinaryPath().contains("/system/");
        StaticContentItem staticContentItem = new StaticContentItem(contentType,
                file,
                versioned);
        return staticContentItem;
    }

    private void refreshBinary(File file, ImageUtils.StaticContentPathInfo pathInfo, BinaryComponent binaryComponent) throws ContentProviderException {
        String downloadUrl = binaryComponent.getVariants().getEdges().get(0).getNode().getDownloadUrl();

        byte[] content = contentDownloader.downloadContent(file, downloadUrl);

        refreshBinary(file, pathInfo, content);
    }

    @Override
    protected String _resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException {
        int publicationId = Integer.parseInt(requestDto.getLocalizationId());
        ContextData contextData = createContextData(requestDto.getClaims());
        Publication publication = pcaClientProvider.getClient().getPublication(ContentNamespace.Sites,
                publicationId,
                "",
                contextData);
        return publication.getPublicationUrl();
    }

    public void setPcaClientProvider(ApiClientProvider pcaClientProvider) {
        this.pcaClientProvider = pcaClientProvider;
    }

    public void setContentDownloader(BinaryContentDownloader contentDownloader) {
        this.contentDownloader = contentDownloader;
    }
}

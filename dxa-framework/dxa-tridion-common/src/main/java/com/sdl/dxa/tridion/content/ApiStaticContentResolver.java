package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.pcaclient.PCAClientProvider;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.BinaryComponent;
import com.sdl.web.pca.client.contentmodel.generated.Publication;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import com.sdl.webapp.common.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;

import static com.sdl.dxa.tridion.common.ContextDataCreator.createContextData;
import static com.sdl.webapp.common.util.FileUtils.isToBeRefreshed;

@Slf4j
@Service
@Profile("!cil.providers.active")
public class ApiStaticContentResolver extends GenericStaticContentResolver implements StaticContentResolver {

    private static final Object LOCK = new Object();

    @Autowired
    private PCAClientProvider pcaClientProvider;

    @Autowired
    public ApiStaticContentResolver(WebApplicationContext webApplicationContext) {
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

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(downloadUrl);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
            byte[] content = IOUtils.toByteArray(response.getEntity().getContent());
            refreshBinary(file, pathInfo, content);
        } catch (IOException e) {
            throw new StaticContentNotLoadedException("Cannot content for file " + file, e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                throw new StaticContentNotLoadedException("Cannot content for file " + file, e);
            }
        }
    }

    @Override
    protected String _resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException {
        int publicationId = Integer.parseInt(requestDto.getLocalizationId());
        Publication publication = pcaClientProvider.getClient().getPublication(ContentNamespace.Sites,
                publicationId,
                "",
                createContextData(requestDto.getClaims()));
        return publication.getPublicationUrl();
    }
}

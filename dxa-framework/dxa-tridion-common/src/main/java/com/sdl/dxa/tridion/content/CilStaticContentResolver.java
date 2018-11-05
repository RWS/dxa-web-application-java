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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;

import static com.sdl.webapp.common.util.FileUtils.isToBeRefreshed;

/**
 * Static content resolver is capable to resolve static (also versioned) binary content from broker database, and to cache it for same request.
 *
 * @dxa.publicApi
 */
@Slf4j
@Service
@Profile("cil.providers.active")
public class CilStaticContentResolver extends GenericStaticContentResolver implements StaticContentResolver {

    private final DynamicMetaRetriever dynamicMetaRetriever = new DynamicMetaRetriever();

    private final BinaryFactory binaryContentRetriever = new BinaryFactory();

    private final PublicationMetaFactory publicationMetaFactory = new PublicationMetaFactory();

    private static final Object LOCK = new Object();

    @Autowired
    public CilStaticContentResolver(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @NotNull
    @Override
    protected StaticContentItem createStaticContentItem(final StaticContentRequestDto requestDto,
                                                        final File file,
                                                        int publicationId,
                                                        ImageUtils.StaticContentPathInfo pathInfo,
                                                        String urlPath) throws ContentProviderException {
        BinaryMeta binaryMeta;

        synchronized (LOCK) {
            binaryMeta = getBinaryMeta(urlPath, publicationId);

            int itemId = (int) binaryMeta.getURI().getItemId();
            ComponentMeta componentMeta = getComponentMeta(pathInfo, publicationId, itemId);

            long componentTime = componentMeta.getLastPublicationDate().getTime();

            boolean shouldRefresh = isToBeRefreshed(file, componentTime) || requestDto.isNoMediaCache();

            if (shouldRefresh) {
                refreshBinary(file, pathInfo, publicationId, binaryMeta, itemId);
            } else {
                log.debug("File does not need to be refreshed: {}", file);
            }
        }

        String contentType = StringUtils.isEmpty(binaryMeta.getType()) ? DEFAULT_CONTENT_TYPE : binaryMeta.getType();
        boolean versioned = requestDto.getBinaryPath().contains("/system/");
        StaticContentItem staticContentItem = new StaticContentItem(contentType,
                file,
                versioned);
        return staticContentItem;
    }

    @Override
    protected String _resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException {
        PublicationMeta meta;
        String localizationId = requestDto.getLocalizationId();
        try {
            meta = publicationMetaFactory.getMeta(TcmUtils.buildPublicationTcmUri(localizationId));
        } catch (StorageException e) {
            throw new StaticContentNotLoadedException("Cannot resolve localization path for localization '" + localizationId + "'", e);
        }
        log.debug("Resolved url '{}' for publication id {}", meta.getPublicationPath(), localizationId);
        return meta.getPublicationUrl();
    }

    @NotNull
    private ComponentMeta getComponentMeta(ImageUtils.StaticContentPathInfo pathInfo, int publicationId, int itemId) throws StaticContentNotFoundException {
        WebComponentMetaFactory factory = new WebComponentMetaFactoryImpl(publicationId);
        ComponentMeta componentMeta = factory.getMeta(itemId);
        if (componentMeta == null) {
            throw new StaticContentNotFoundException("No meta meta found for: [" + publicationId + "] " +
                    pathInfo.getFileName());
        }
        return componentMeta;
    }

    @NotNull
    private BinaryMeta getBinaryMeta(String urlPath, int publicationId) throws StaticContentNotFoundException {
        BinaryMeta binaryMeta = dynamicMetaRetriever.getBinaryMetaByURL(urlPath);
        if (binaryMeta == null) {
            throw new StaticContentNotFoundException("No binary meta found for pubId: [" +
                    publicationId + "] and urlPath: " + urlPath);
        }
        return binaryMeta;
    }

    private void refreshBinary(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId, BinaryMeta binaryMeta, int itemId) throws ContentProviderException {
        try {
            BinaryData binaryData = binaryContentRetriever.getBinary(publicationId, itemId, binaryMeta.getVariantId());
            refreshBinary(file, pathInfo, binaryData.getBytes());
        } catch (IOException e) {
            throw new StaticContentNotLoadedException("Cannot write new loaded content to a file " + file, e);
        }
    }
}

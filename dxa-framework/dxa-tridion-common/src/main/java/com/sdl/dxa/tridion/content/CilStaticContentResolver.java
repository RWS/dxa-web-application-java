package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import com.sdl.webapp.common.impl.model.ContentNamespace;
import com.sdl.webapp.common.util.ImageUtils;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.broker.StorageException;
import com.tridion.content.BinaryFactory;
import com.tridion.data.BinaryData;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.ComponentMetaFactory;
import com.tridion.meta.PublicationMeta;
import com.tridion.meta.PublicationMetaFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;

import static com.sdl.webapp.common.util.FileUtils.isToBeRefreshed;

/**
 * Static content resolver is capable to resolve static (also versioned) binary content from broker database,
 * and to cache it for same request.
 *
 * @dxa.publicApi
 * @deprecated since PCA implementation added which supports mashup scenario.
 */
@Slf4j
@Service("cilStaticContentResolver")
@Profile("cil.providers.active")
@Deprecated
public class CilStaticContentResolver extends GenericStaticContentResolver implements StaticContentResolver {

    private final DynamicMetaRetriever dynamicMetaRetriever;

    private final BinaryFactory binaryFactory;

    private final PublicationMetaFactory webPublicationMetaFactory;

    @Autowired
    public CilStaticContentResolver(WebApplicationContext webApplicationContext,
                                    DynamicMetaRetriever dynamicMetaRetriever,
                                    BinaryFactory binaryFactory,
                                    PublicationMetaFactory publicationMetaFactory) {
        this.webApplicationContext = webApplicationContext;
        this.dynamicMetaRetriever = dynamicMetaRetriever;
        this.binaryFactory = binaryFactory;
        this.webPublicationMetaFactory = publicationMetaFactory;
    }

    @NotNull
    protected StaticContentItem createStaticContentItem(
                                                        StaticContentRequestDto requestDto,
                                                        File file,
                                                        int publicationId,
                                                        ImageUtils.StaticContentPathInfo pathInfo,
                                                        String urlPath) throws ContentProviderException {
        return createStaticContentItem(null, requestDto, file, publicationId, pathInfo, urlPath);
    }

    @Override
    protected @NotNull StaticContentItem createStaticContentItem(ContentNamespace namespace, StaticContentRequestDto requestDto, File file, int publicationId, ImageUtils.StaticContentPathInfo pathInfo, String urlPath) throws ContentProviderException {
        BinaryMeta binaryMeta = getBinaryMeta(urlPath, publicationId);

        int itemId = (int) binaryMeta.getURI().getItemId();
        ComponentMeta componentMeta = getComponentMeta(pathInfo, publicationId, itemId);

        long componentTime = componentMeta.getLastPublicationDate().getTime();

        boolean shouldRefresh = requestDto.isNoMediaCache() || isToBeRefreshed(file, componentTime);

        if (shouldRefresh) {
            log.debug("File needs to be refreshed: {}", file.getAbsolutePath());
            refreshBinary(file, pathInfo, publicationId, binaryMeta, itemId);
        } else {
            log.debug("File does not need to be refreshed: {}", file.getAbsolutePath());
        }

        String contentType = StringUtils.isEmpty(binaryMeta.getType()) ? DEFAULT_CONTENT_TYPE : binaryMeta.getType();
        boolean versioned = requestDto.getBinaryPath().contains("/system/");
        return new StaticContentItem(contentType, file, versioned);
        }

    @Override
    protected @NotNull StaticContentItem getStaticContentItemById(ContentNamespace namespace, int binaryId, StaticContentRequestDto requestDto) throws ContentProviderException {
        throw new NotImplementedException("This is not implemented in CIL. Use GraphQL instead of CIL.");
    }

    @Override
    protected String resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException {
        return resolveLocalizationPath(requestDto, ContentNamespace.Sites);
    }

    @Override
    protected String resolveLocalizationPath(StaticContentRequestDto requestDto, ContentNamespace namespace) throws StaticContentNotLoadedException {
        String localizationId = requestDto.getLocalizationId();
        try {
            PublicationMeta meta = webPublicationMetaFactory.getMeta(TcmUtils.buildPublicationTcmUri(localizationId));
            log.debug("Resolved url '{}' for publication id {}", meta.getPublicationPath(), localizationId);
            return meta.getPublicationUrl();
        } catch (StorageException e) {
            throw new StaticContentNotLoadedException("Cannot resolve localization path for localization '" + localizationId + "'", e);
        }
    }

    @NotNull
    private ComponentMeta getComponentMeta(ImageUtils.StaticContentPathInfo pathInfo, int publicationId, int itemId) throws StaticContentNotFoundException {
        ComponentMetaFactory factory = new ComponentMetaFactory(publicationId);
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
        Triple<Integer, Integer, String> key = new ImmutableTriple<>(publicationId, itemId, binaryMeta.getVariantId());
        try {
            synchronized (key.toString().intern()) {
                BinaryData binaryData = binaryFactory.getBinary(publicationId, itemId, binaryMeta.getVariantId());
                refreshBinary(file, pathInfo, binaryData.getBytes());
            }
        } catch (IOException e) {
            throw new StaticContentNotLoadedException("Cannot write new loaded content to a file " + file, e);
        }
    }
}

package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.util.ImageUtils;
import com.sdl.webapp.tridion.query.BrokerQuery;
import com.sdl.webapp.tridion.query.BrokerQueryImpl;
import com.tridion.content.BinaryFactory;
import com.tridion.data.BinaryData;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.ComponentMetaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
/**
 * <p>DefaultProvider class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class DefaultProvider extends AbstractDefaultProvider {
    private static final Object LOCK = new Object();
    private static final Logger LOG = LoggerFactory.getLogger(DefaultProvider.class);

    @Autowired
    private DynamicMetaRetriever dynamicMetaRetriever;

    @Autowired
    private BinaryFactory binaryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("Duplicates")
    protected DefaultProvider.StaticContentFile getStaticContentFile(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId) throws ContentProviderException, IOException {
        BinaryMeta binaryMeta;
        ComponentMetaFactory factory = new ComponentMetaFactory(publicationId);
        ComponentMeta componentMeta;
        int itemId;

        synchronized (LOCK) {
            binaryMeta = dynamicMetaRetriever.getBinaryMetaByURL(prependFullUrlIfNeeded(pathInfo.getFileName()));
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
        if (isToBeRefreshed(file, componentTime)) {
            BinaryData binaryData = binaryFactory.getBinary(publicationId, itemId, binaryMeta.getVariantId());

            LOG.debug("Writing binary content to file: {}", file);
            writeToFile(file, pathInfo, binaryData.getBytes());
        } else {
            LOG.debug("File does not need to be refreshed: {}", file);
        }

        return new StaticContentFile(file, binaryMeta.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BrokerQuery instantiateBrokerQuery() {
        return new BrokerQueryImpl();
    }

}

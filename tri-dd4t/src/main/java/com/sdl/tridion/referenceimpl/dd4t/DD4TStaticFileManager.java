package com.sdl.tridion.referenceimpl.dd4t;

import com.sdl.tridion.referenceimpl.common.BaseStaticFileManager;
import com.tridion.broker.StorageException;
import com.tridion.storage.*;
import com.tridion.storage.dao.BinaryContentDAO;
import com.tridion.storage.dao.BinaryVariantDAO;
import com.tridion.storage.dao.ItemDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
public class DD4TStaticFileManager extends BaseStaticFileManager {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TStaticFileManager.class);

    // TODO: Publication id should be determined from configuration instead of being hard-coded
    private static final int PUBLICATION_ID = 48;

    @Override
    public boolean getStaticContent(String url, File destinationFile) throws IOException {
        try {
            final BinaryVariant binaryVariant = findBinaryVariant(PUBLICATION_ID, url);
            if (binaryVariant == null) {
                LOG.debug("No binary variant found for: {}", url);
                return false;
            }

            final BinaryMeta binaryMeta = binaryVariant.getBinaryMeta();
            final ItemMeta itemMeta = findItemMeta(binaryMeta.getPublicationId(), binaryMeta.getItemId());

            boolean refresh;
            if (destinationFile.exists()) {
                refresh = destinationFile.lastModified() < itemMeta.getLastPublishDate().getTime();
            } else {
                refresh = true;
                if (!destinationFile.getParentFile().exists()) {
                    if (!destinationFile.getParentFile().mkdirs()) {
                        throw new IOException("Failed to create parent directory for file: " + destinationFile);
                    }
                }
            }

            if (refresh) {
                final BinaryContent binaryContent = findBinaryContent(itemMeta.getPublicationId(), itemMeta.getItemId(),
                        binaryVariant.getVariantId());

                LOG.debug("Writing binary content to file: {}", destinationFile);
                Files.write(destinationFile.toPath(), binaryContent.getContent());
            } else {
                LOG.debug("File does not need to be refreshed: {}", destinationFile);
            }

            return true;
        } catch (StorageException e) {
            throw new IOException("Error while getting static content: " + url, e);
        }

    }

    private BinaryVariant findBinaryVariant(int publicationId, String url) throws StorageException {
        final BinaryVariantDAO dao = (BinaryVariantDAO) StorageManagerFactory.getDAO(publicationId,
                StorageTypeMapping.BINARY_VARIANT);
        final List<BinaryVariant> binaryVariants = dao.findByURL(url);
        if (binaryVariants == null || binaryVariants.isEmpty()) {
            return null;
        }

        return binaryVariants.get(0);
    }

    private ItemMeta findItemMeta(int publicationId, int itemId) throws StorageException {
        final ItemDAO dao = (ItemDAO) StorageManagerFactory.getDAO(PUBLICATION_ID, StorageTypeMapping.ITEM_META);
        return dao.findByPrimaryKey(publicationId, itemId);
    }

    private BinaryContent findBinaryContent(int publicationId, int itemId, String variantId) throws StorageException {
        final BinaryContentDAO dao = (BinaryContentDAO) StorageManagerFactory.getDAO(PUBLICATION_ID,
                StorageTypeMapping.BINARY_CONTENT);
        return dao.findByPrimaryKey(publicationId, itemId, variantId);
    }
}

package com.sdl.webapp.dd4t;

import com.sdl.webapp.common.api.*;
import com.tridion.broker.StorageException;
import com.tridion.storage.*;
import com.tridion.storage.dao.BinaryContentDAO;
import com.tridion.storage.dao.BinaryVariantDAO;
import com.tridion.storage.dao.ItemDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@Component
public class DD4TStaticContentProvider implements StaticContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TStaticContentProvider.class);

    private static final String STATIC_FILES_DIR = "BinaryData";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Override
    public StaticContentItem getStaticContent(String url, Localization localization) throws ContentProviderException {
        final File file = getStaticContentFile(url, localization);

        return new StaticContentItem() {
            @Override
            public long getLastModified() {
                return file.lastModified();
            }

            @Override
            public InputStream getContent() throws IOException {
                return new FileInputStream(file);
            }
        };
    }

    private File getStaticContentFile(String url, Localization localization) throws ContentProviderException {
        final File file = new File(new File(new File(new File(new File(
                webApplicationContext.getServletContext().getRealPath("/")), STATIC_FILES_DIR), localization.getId()),
                localization.getPath()), url);

        final int publicationId = Integer.parseInt(localization.getId());
        try {
            final BinaryVariant binaryVariant = findBinaryVariant(publicationId, url);
            if (binaryVariant == null) {
                throw new StaticContentNotFoundException("No binary variant found for: [" + publicationId + "] " + url);
            }

            final BinaryMeta binaryMeta = binaryVariant.getBinaryMeta();
            final ItemMeta itemMeta = findItemMeta(binaryMeta.getPublicationId(), binaryMeta.getItemId());

            boolean refresh;
            if (file.exists()) {
                refresh = file.lastModified() < itemMeta.getLastPublishDate().getTime();
            } else {
                refresh = true;
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        throw new ContentProviderException("Failed to create parent directory for file: " + file);
                    }
                }
            }

            if (refresh) {
                final BinaryContent binaryContent = findBinaryContent(itemMeta.getPublicationId(), itemMeta.getItemId(),
                        binaryVariant.getVariantId());

                LOG.debug("Writing binary content to file: {}", file);
                Files.write(file.toPath(), binaryContent.getContent());
            } else {
                LOG.debug("File does not need to be refreshed: {}", file);
            }

            return file;
        } catch (StorageException | IOException e) {
            throw new ContentProviderException("Exception while getting static content for: [" + publicationId + "] "
                    + url, e);
        }
    }

    private BinaryVariant findBinaryVariant(int publicationId, String url) throws StorageException {
        final BinaryVariantDAO dao = (BinaryVariantDAO) StorageManagerFactory.getDAO(publicationId,
                StorageTypeMapping.BINARY_VARIANT);
        final List<BinaryVariant> binaryVariants = dao.findByURL(url);
        return binaryVariants != null && !binaryVariants.isEmpty() ? binaryVariants.get(0) : null;
    }

    private ItemMeta findItemMeta(int publicationId, int itemId) throws StorageException {
        final ItemDAO dao = (ItemDAO) StorageManagerFactory.getDAO(publicationId, StorageTypeMapping.ITEM_META);
        return dao.findByPrimaryKey(publicationId, itemId);
    }

    private BinaryContent findBinaryContent(int publicationId, int itemId, String variantId) throws StorageException {
        final BinaryContentDAO dao = (BinaryContentDAO) StorageManagerFactory.getDAO(publicationId,
                StorageTypeMapping.BINARY_CONTENT);
        return dao.findByPrimaryKey(publicationId, itemId, variantId);
    }
}

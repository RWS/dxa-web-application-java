package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.content.StaticContentProvider;
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
import java.util.regex.Pattern;

/**
 * Implementation of {@code StaticContentProvider} that uses DD4T to provide static content.
 *
 * TODO: Should use DD4T BinaryFactory instead of calling the Tridion broker API directly.
 */
@Component
public class DD4TStaticContentProvider implements StaticContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TStaticContentProvider.class);

    private static final String STATIC_FILES_DIR = "BinaryData";

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final WebApplicationContext webApplicationContext;

    @Autowired
    public DD4TStaticContentProvider(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    public StaticContentItem getStaticContent(String path, String localizationId, String localizationPath)
            throws ContentProviderException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("getStaticContent: {} [{}] {}", new Object[] { path, localizationId, localizationPath });
        }

        final File file = getStaticContentFile(removeVersionNumber(path), localizationId, localizationPath);
        final String mimeType = determineMimeType(file);

        return new StaticContentItem() {
            @Override
            public long getLastModified() {
                return file.lastModified();
            }

            @Override
            public String getMimeType() {
                return mimeType;
            }

            @Override
            public InputStream getContent() throws IOException {
                return new FileInputStream(file);
            }
        };
    }

    private String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    private File getStaticContentFile(String path, String localizationId, String localizationPath)
            throws ContentProviderException {
        final File file = new File(new File(new File(new File(new File(
                webApplicationContext.getServletContext().getRealPath("/")), STATIC_FILES_DIR), localizationId),
                localizationPath), path);
        LOG.trace("getStaticContentFile: {}", file);

        final int publicationId = Integer.parseInt(localizationId);
        try {
            final BinaryVariant binaryVariant = findBinaryVariant(publicationId, path);
            if (binaryVariant == null) {
                throw new StaticContentNotFoundException("No binary variant found for: [" + publicationId + "] " + path);
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
                    + path, e);
        }
    }

    private BinaryVariant findBinaryVariant(int publicationId, String path) throws StorageException {
        final BinaryVariantDAO dao = (BinaryVariantDAO) StorageManagerFactory.getDAO(publicationId,
                StorageTypeMapping.BINARY_VARIANT);
        final List<BinaryVariant> binaryVariants = dao.findByURL(path);
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

    private String determineMimeType(File file) {
        try {
            final String contentType = Files.probeContentType(file.toPath());
            return !Strings.isNullOrEmpty(contentType) ? contentType : DEFAULT_CONTENT_TYPE;
        } catch (IOException e) {
            LOG.warn("Failed to determine content type of file: {}", file.getPath(), e);
            return DEFAULT_CONTENT_TYPE;
        }
    }
}

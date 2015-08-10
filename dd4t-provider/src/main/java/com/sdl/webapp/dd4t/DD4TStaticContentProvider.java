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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
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

    private static final class StaticContentFile {
        private final File file;
        private final String contentType;

        private StaticContentFile(File file, String contentType) {
            this.file = file;
            this.contentType = Strings.isNullOrEmpty(contentType) ? DEFAULT_CONTENT_TYPE : contentType;
        }

        public File getFile() {
            return file;
        }

        public String getContentType() {
            return contentType;
        }
    }

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

        final String contentPath;
        if (localizationPath.length() > 1) {
            contentPath = localizationPath + removeVersionNumber(path.startsWith(localizationPath) ?
                    path.substring(localizationPath.length()) : path);
        } else {
            contentPath = removeVersionNumber(path);
        }

        final StaticContentFile staticContentFile = getStaticContentFile(contentPath, localizationId);

        return new StaticContentItem() {
            @Override
            public long getLastModified() {
                return staticContentFile.getFile().lastModified();
            }

            @Override
            public String getContentType() {
                return staticContentFile.getContentType();
            }

            @Override
            public InputStream getContent() throws IOException {
                return new FileInputStream(staticContentFile.getFile());
            }
        };
    }

    private String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    private StaticContentFile getStaticContentFile(String path, String localizationId)
            throws ContentProviderException {
        final File file = new File(new File(new File(new File(
                webApplicationContext.getServletContext().getRealPath("/")), STATIC_FILES_DIR), localizationId), path);
        LOG.trace("getStaticContentFile: {}", file);

        final StaticContentPathInfo pathInfo = new StaticContentPathInfo(path);

        final int publicationId = Integer.parseInt(localizationId);
        try {
            final BinaryVariant binaryVariant = findBinaryVariant(publicationId, pathInfo.getFileName());
            if (binaryVariant == null) {
                throw new StaticContentNotFoundException("No binary variant found for: [" + publicationId + "] " +
                        pathInfo.getFileName());
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

                byte[] content = binaryContent.getContent();
                if (pathInfo.isImage() && pathInfo.isResized()) {
                    content = resizeImage(content, pathInfo);
                }

                LOG.debug("Writing binary content to file: {}", file);
                Files.write(file.toPath(), content);
            } else {
                LOG.debug("File does not need to be refreshed: {}", file);
            }

            return new StaticContentFile(file, binaryVariant.getBinaryType());
        } catch (StorageException | IOException e) {
            throw new ContentProviderException("Exception while getting static content for: [" + publicationId + "] "
                    + path, e);
        }
    }

    private BinaryVariant findBinaryVariant(int publicationId, String path) throws StorageException {
        final BinaryVariantDAO dao = (BinaryVariantDAO) StorageManagerFactory.getDAO(publicationId,
                StorageTypeMapping.BINARY_VARIANT);
        return dao.findByURL(publicationId, path);
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

    private byte[] resizeImage(byte[] original, StaticContentPathInfo pathInfo) throws ContentProviderException {
        try {
            final BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(original));

            int cropX = 0, cropY = 0;
            int sourceW = originalImage.getWidth(), sourceH = originalImage.getHeight();
            int targetW, targetH;

            // Most complex case is if a height AND width is specified
            if (pathInfo.getWidth() > 0 && pathInfo.getHeight() > 0) {
                if (pathInfo.isNoStretch()) {
                    // If we don't want to stretch, then we crop
                    float originalAspect = (float) sourceW / (float) sourceH;
                    float targetAspect = (float) pathInfo.getWidth() / (float) pathInfo.getHeight();
                    if (targetAspect < originalAspect) {
                        // Crop the width - ensuring that we do not stretch if the requested height is bigger than the original
                        targetH = Math.min(pathInfo.getHeight(), sourceH); // pathInfo.getHeight() > sourceH ? sourceH : pathInfo.getHeight();
                        targetW = (int) Math.ceil(targetH * targetAspect);
                        cropX = (int) Math.ceil((sourceW - (sourceH * targetAspect)) / 2);
                        sourceW = sourceW - 2 * cropX;
                    } else {
                        // Crop the height - ensuring that we do not stretch if the requested width is bigger than the original
                        targetW = Math.min(pathInfo.getWidth(), sourceW); // pathInfo.getWidth() > sourceW ? sourceW : pathInfo.getWidth();
                        targetH = (int) Math.ceil(targetW / targetAspect);
                        cropY = (int) Math.ceil((sourceH - (sourceW / targetAspect)) / 2);
                        sourceH = sourceH - 2 * cropY;
                    }
                } else {
                    // We stretch to fit the dimensions
                    targetH = pathInfo.getHeight();
                    targetW = pathInfo.getWidth();
                }
            } else if (pathInfo.getWidth() > 0) {
                // If we simply have a certain width or height, its simple: We just use that and derive the other
                // dimension from the original image aspect ratio. We also check if the target size is bigger than
                // the original, and if we allow stretching.
                targetW = (pathInfo.isNoStretch() && pathInfo.getWidth() > sourceW) ?
                        sourceW : pathInfo.getWidth();
                targetH = (int)(sourceH * ((float) targetW / (float) sourceW));
            } else {
                targetH = (pathInfo.isNoStretch() && pathInfo.getHeight() > sourceH) ?
                        sourceH : pathInfo.getHeight();
                targetW = (int)(sourceW * ((float) targetH / (float) sourceH));
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Image: {}, cropX = {}, cropY = {}, sourceW = {}, sourceH = {}, targetW = {}, targetH = {}",
                        new Object[]{ pathInfo.getFileName(), cropX, cropY, sourceW, sourceH, targetW, targetH });
            }

            if (targetW == sourceW && targetH == sourceH) {
                // No resize required
                return original;
            }

            final BufferedImage target = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);

            final Graphics2D graphics = target.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            final AffineTransform transform = new AffineTransform();
            transform.scale((double) targetW / (double) sourceW, (double) targetH / (double) sourceH);
            transform.translate(-cropX, -cropY);

            graphics.drawRenderedImage(originalImage, transform);

            graphics.dispose();

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(target, pathInfo.getImageFormatName(), out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ContentProviderException("Exception while processing image data", e);
        }
    }
}

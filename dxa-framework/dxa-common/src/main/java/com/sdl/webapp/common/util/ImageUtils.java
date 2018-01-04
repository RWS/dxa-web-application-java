package com.sdl.webapp.common.util;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.sdl.webapp.common.api.content.ContentProviderException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @dxa.publicApi
 */
@Slf4j
public final class ImageUtils {

    private ImageUtils() {
    }

    public static byte[] resizeImage(byte[] original, StaticContentPathInfo pathInfo) throws ContentProviderException {
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
                targetH = (int) (sourceH * ((float) targetW / (float) sourceW));
            } else {
                targetH = (pathInfo.isNoStretch() && pathInfo.getHeight() > sourceH) ?
                        sourceH : pathInfo.getHeight();
                targetW = (int) (sourceW * ((float) targetH / (float) sourceH));
            }

            if (log.isDebugEnabled()) {
                log.debug("Image: {}, cropX = {}, cropY = {}, sourceW = {}, sourceH = {}, targetW = {}, targetH = {}",
                        pathInfo.getFileName(), cropX, cropY, sourceW, sourceH, targetW, targetH);
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

    public static void writeToFile(File file, ImageUtils.StaticContentPathInfo pathInfo, byte[] content) throws ContentProviderException, IOException {
        if (pathInfo.isImage() && pathInfo.isResized()) {
            content = ImageUtils.resizeImage(content, pathInfo);
        }

        Files.write(content, file);
    }

    public static final class StaticContentPathInfo {

        private static final Pattern IMAGE_FILENAME_PATTERN = Pattern.compile("(.*)_w([\\d]+)(?:_h([\\d]+))?(_n)?(\\.[^\\.]+)?");

        private final String fileName;

        private final String imageFormatName;

        private final boolean isImage;

        private final int width;

        private final int height;

        private final boolean noStretch;

        public StaticContentPathInfo(String path) {
            final Matcher matcher = IMAGE_FILENAME_PATTERN.matcher(path);
            if (matcher.matches()) {
                final String baseName = matcher.group(1);
                final String widthString = matcher.group(2);
                final String heightString = matcher.group(3);
                final String noStretchString = matcher.group(4);
                final String extension = matcher.group(5);

                this.isImage = true;
                this.fileName = extension != null ? baseName + extension : baseName;
                this.width = !Strings.isNullOrEmpty(widthString) ? Integer.parseInt(widthString) : 0;
                this.height = !Strings.isNullOrEmpty(heightString) ? Integer.parseInt(heightString) : 0;
                this.noStretch = noStretchString != null;
                this.imageFormatName = extension != null ? extension.substring(1) : null;
            } else {
                this.fileName = path;
                this.isImage = false;
                this.width = 0;
                this.height = 0;
                this.noStretch = false;
                this.imageFormatName = null;
            }
        }

        public String getFileName() {
            return fileName;
        }

        public boolean isImage() {
            return isImage;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean isNoStretch() {
            return noStretch;
        }

        public boolean isResized() {
            return width != 0 || height != 0;
        }

        public String getImageFormatName() {
            return imageFormatName;
        }
    }
}

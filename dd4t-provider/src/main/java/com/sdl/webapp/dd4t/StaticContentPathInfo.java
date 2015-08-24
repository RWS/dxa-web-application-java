package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class StaticContentPathInfo {

    private static final Pattern IMAGE_FILENAME_PATTERN = Pattern.compile("(.*)_w([\\d]+)(?:_h([\\d]+))?(_n)?(\\.[^\\.]+)?");

    private final String fileName;
    private final String imageFormatName;

    private final boolean isImage;
    private final int width;
    private final int height;
    private final boolean noStretch;

    StaticContentPathInfo(String path) {
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

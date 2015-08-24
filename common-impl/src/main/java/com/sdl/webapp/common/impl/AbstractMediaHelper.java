package com.sdl.webapp.common.impl;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of {@code MediaHelper} with common functionality.
 */
public abstract class AbstractMediaHelper implements MediaHelper {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMediaHelper.class);

    private static final int GRID_SIZE = 12;

    private static final int SMALL_SCREEN_BREAKPOINT = 480;
    private static final int MEDIUM_SCREEN_BREAKPOINT = 940;
    private static final int LARGE_SCREEN_BREAKPOINT = 1140;

    // Default media aspect is the golden ratio
    private static final double DEFAULT_MEDIA_ASPECT = 1.62;

    private static final String DEFAULT_MEDIA_FILL = "100%";

    private static final int[] IMAGE_WIDTHS = { 160, 320, 640, 1024, 2048 };

    private final WebRequestContext webRequestContext;

    protected AbstractMediaHelper(WebRequestContext webRequestContext) {
        this.webRequestContext = webRequestContext;
    }

    @Override
    public int getResponsiveWidth(String widthFactor, int containerSize) {
        final int gridSize = getGridSize();
        final String defaultMediaFill = getDefaultMediaFill();

        if (Strings.isNullOrEmpty(widthFactor)) {
            widthFactor = defaultMediaFill;
        }

        if (containerSize == 0) {
            containerSize = gridSize;
        }

        double width = 0.0;

        if (!widthFactor.endsWith("%")) {
            try {
                final double pixelRatio = webRequestContext.getPixelRatio();

                width = Double.parseDouble(widthFactor) * pixelRatio;
            } catch (NumberFormatException e) {
                LOG.warn("Invalid width factor (\"{}\") when resizing image, defaulting to {}", widthFactor, defaultMediaFill);
                widthFactor = defaultMediaFill;
            }
        }

        if (widthFactor.endsWith("%")) {
            int fillFactor = 0;
            try {
                fillFactor = Integer.parseInt(widthFactor.substring(0, widthFactor.length() - 1));
            } catch (NumberFormatException e) {
                LOG.warn("Invalid width factor (\"{}\") when resizing image, defaulting to {}", widthFactor, defaultMediaFill);
            }

            if (fillFactor == 0) {
                fillFactor = Integer.parseInt(defaultMediaFill.substring(0, defaultMediaFill.length() - 1));
            }

            // Adjust container size for extra small and small screens
            switch (getScreenWidth()) {
                case EXTRA_SMALL:
                    // Extra small screens are only one column
                    containerSize = gridSize;
                    break;

                case SMALL:
                    // Small screens are max 2 columns
                    containerSize = containerSize <= gridSize / 2 ? gridSize / 2 : gridSize;
                    break;
            }

            int cols = gridSize / containerSize;
            int padding = (cols - 1) * 30;

            width = (fillFactor * containerSize * webRequestContext.getMaxMediaWidth() / (gridSize * 100)) - padding;
        }

        return (int) Math.ceil(width);
    }

    @Override
    public int getResponsiveHeight(String widthFactor, double aspect, int containerSize) {
        return (int) Math.ceil(getResponsiveWidth(widthFactor, containerSize) / aspect);
    }

    @Override
    public int getGridSize() {
        return GRID_SIZE;
    }

    @Override
    public ScreenWidth getScreenWidth() {
        final int displayWidth = webRequestContext.getDisplayWidth();
        if (displayWidth < SMALL_SCREEN_BREAKPOINT) {
            return ScreenWidth.EXTRA_SMALL;
        } else if (displayWidth < MEDIUM_SCREEN_BREAKPOINT) {
            return ScreenWidth.SMALL;
        } else if (displayWidth < LARGE_SCREEN_BREAKPOINT) {
            return ScreenWidth.MEDIUM;
        } else {
            return ScreenWidth.LARGE;
        }
    }

    @Override
    public int getSmallScreenBreakpoint() {
        return SMALL_SCREEN_BREAKPOINT;
    }

    @Override
    public int getMediumScreenBreakpoint() {
        return MEDIUM_SCREEN_BREAKPOINT;
    }

    @Override
    public int getLargeScreenBreakpoint() {
        return LARGE_SCREEN_BREAKPOINT;
    }

    @Override
    public double getDefaultMediaAspect() {
        return DEFAULT_MEDIA_ASPECT;
    }

    @Override
    public String getDefaultMediaFill() {
        return DEFAULT_MEDIA_FILL;
    }

    protected int roundWidth(int width) {
        // Round the width to the nearest set limit point - important as we do not want to swamp the cache
        // with lots of different sized versions of the same image
        for (int i = 0; i < IMAGE_WIDTHS.length; i++) {
            if (width <= IMAGE_WIDTHS[i] || i == IMAGE_WIDTHS.length - 1) {
                return IMAGE_WIDTHS[i];
            }
        }

        // Note that this point will never be reached in practice
        return width;
    }
}

package com.sdl.webapp.common.impl;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultMediaHelper implements MediaHelper {

    // TODO: Have the grid size configurable from the CMS settings
    private static final int GRID_SIZE = 12;
    // Default media aspect is the golden ratio
    private static final double DEFAULT_MEDIA_ASPECT = 1.62;
    private static final String DEFAULT_MEDIA_FILL = "100%";
    private static final int[] IMAGE_WIDTHS = {160, 320, 640, 1024, 2048};

    @Autowired
    private WebRequestContext webRequestContext;
    @Autowired
    private ResponsiveMediaUrlBuilder responsiveMediaUrlBuilder;

    /**
     * <p>Round the width to the nearest set limit point - important as we do not want to swamp the cache
     * with lots of different sized versions of the same image.</p>
     *
     * @param width initial width of image requested
     * @return the nearest (upper) point.
     */
    protected static int roundWidth(int width) {

        for (int imageWidth : IMAGE_WIDTHS) {
            if (width <= imageWidth) {
                return imageWidth;
            }
        }

        return IMAGE_WIDTHS[IMAGE_WIDTHS.length - 1];
    }

    /**
     * <p>divideByAspect.</p>
     *
     * @param dimension a int.
     * @param aspect    a double.
     * @return a int.
     */
    protected static int divideByAspect(int dimension, double aspect) {
        return (int) Math.ceil(dimension / aspect);
    }

    /** {@inheritDoc} */
    @Override
    public int getResponsiveWidth(String widthFactor, int containerSize) {
        final int gridSize = GRID_SIZE;
        final String defaultMediaFill = DEFAULT_MEDIA_FILL;

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
                log.warn("Invalid width factor (\"{}\") when resizing image, defaulting to {}", widthFactor, defaultMediaFill);
                widthFactor = defaultMediaFill;
            }
        }

        if (widthFactor.endsWith("%")) {
            int fillFactor = 0;
            try {
                fillFactor = Integer.parseInt(widthFactor.substring(0, widthFactor.length() - 1));
            } catch (NumberFormatException e) {
                log.warn("Invalid width factor (\"{}\") when resizing image, defaulting to {}", widthFactor, defaultMediaFill);
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
                    containerSize = (containerSize <= (gridSize / 2)) ? (gridSize / 2) : gridSize;
                    break;
                default:
                    break;

            }

            int cols = gridSize / containerSize;
            int padding = (cols - 1) * 30;

            width = ((fillFactor * containerSize * webRequestContext.getMaxMediaWidth()) / (gridSize * 100)) - padding;
        }

        return (int) Math.ceil(width);
    }

    /** {@inheritDoc} */
    @Override
    public int getResponsiveHeight(String widthFactor, double aspect, int containerSize) {
        return divideByAspect(getResponsiveWidth(widthFactor, containerSize), aspect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        final int width = roundWidth(getResponsiveWidth(widthFactor, containerSize));

        // Height is calculated from the aspect ratio (0 means preserve aspect ratio)
        boolean aspectIzZero = aspect == 0.0;
        final int height = aspectIzZero ? 0 : divideByAspect(width, aspect);

        return responsiveMediaUrlBuilder
                .newInstance()
                .setBaseUrl(url)
                .setZeroAspect(aspectIzZero)
                .setWidth(String.valueOf(width))
                .setHeight(String.valueOf(height))
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public int getGridSize() {
        return GRID_SIZE;
    }

    /** {@inheritDoc} */
    @Override
    public ScreenWidth getScreenWidth() {
        final int displayWidth = webRequestContext.getDisplayWidth();

        for (ScreenWidth screenWidth : ScreenWidth.values()) {
            if (screenWidth.isThisScreenWidth(displayWidth)) {
                return screenWidth;
            }
        }

        return ScreenWidth.LARGE;
    }

    /** {@inheritDoc} */
    @Override
    public int getSmallScreenBreakpoint() {
        return ScreenWidth.EXTRA_SMALL.getBreakpoint();
    }

    /** {@inheritDoc} */
    @Override
    public int getMediumScreenBreakpoint() {
        return ScreenWidth.SMALL.getBreakpoint();
    }

    /** {@inheritDoc} */
    @Override
    public int getLargeScreenBreakpoint() {
        return ScreenWidth.MEDIUM.getBreakpoint();
    }

    /** {@inheritDoc} */
    @Override
    public double getDefaultMediaAspect() {
        return DEFAULT_MEDIA_ASPECT;
    }

    /** {@inheritDoc} */
    @Override
    public String getDefaultMediaFill() {
        return DEFAULT_MEDIA_FILL;
    }

    @Component
    public static class MediaHelperFactoryImpl implements MediaHelperFactory {

        @Autowired
        private MediaHelper mediaHelper;

        @Override
        public MediaHelper getMediaHelperInstance() {
            return mediaHelper;
        }
    }

    @Component
    protected static class DefaultResponsiveMediaUrlBuilder implements ResponsiveMediaUrlBuilder {

        @Override
        public Builder newInstance() {
            return new DefaultBuilder();
        }

        private static class DefaultBuilder extends Builder {
            @Override
            protected String buildInternal() {
                String url = getBaseUrl();

                final int index = url.lastIndexOf('.');
                final String baseUrl, extension;
                if (index >= 0 && index < url.length() - 1) {
                    baseUrl = url.substring(0, index);
                    extension = url.substring(index);
                } else {
                    baseUrl = url;
                    extension = "";
                }

                return String.format("%s_w%s%s_n%s",
                        baseUrl,
                        getWidth(),
                        isZeroAspect() ? "" : "_h" + getHeight(),
                        extension);
            }
        }
    }
}

package com.sdl.webapp.common.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @dxa.publicApi
 */
public interface MediaHelper {

    /**
     * <p>getResponsiveWidth.</p>
     *
     * @param widthFactor   a {@link java.lang.String} object.
     * @param containerSize a int.
     * @return a int.
     */
    int getResponsiveWidth(String widthFactor, int containerSize);

    /**
     * <p>getResponsiveHeight.</p>
     *
     * @param widthFactor   a {@link java.lang.String} object.
     * @param aspect        a double.
     * @param containerSize a int.
     * @return a int.
     */
    int getResponsiveHeight(String widthFactor, double aspect, int containerSize);

    /**
     * <p>getResponsiveImageUrl.</p>
     *
     * @param url           a {@link java.lang.String} object.
     * @param widthFactor   a {@link java.lang.String} object.
     * @param aspect        a double.
     * @param containerSize a int.
     * @return a {@link java.lang.String} object.
     */
    String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize);

    /**
     * <p>getGridSize.</p>
     *
     * @return a int.
     */
    int getGridSize();

    /**
     * <p>getScreenWidth.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.ScreenWidth} object.
     */
    ScreenWidth getScreenWidth();

    /**
     * <p>Point after which the screen is small.</p>
     *
     * @return a value in pixels.
     */
    int getSmallScreenBreakpoint();

    /**
     * <p>Point after which the screen is medium.</p>
     *
     * @return a value in pixels.
     */
    int getMediumScreenBreakpoint();

    /**
     * <p>Point after which the screen is large.</p>
     *
     * @return a value in pixels.
     */
    int getLargeScreenBreakpoint();

    /**
     * <p>getDefaultMediaAspect.</p>
     *
     * @return a double.
     */
    double getDefaultMediaAspect();

    /**
     * <p>getDefaultMediaFill.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getDefaultMediaFill();

    /**
     * DXA Model uses MediaHelper internally which is not nice though. Also, Model classes are not managed by Spring,
     * so IoC doesn't work which is again not nice.
     * <p>
     * Anyway, in order to support multiple implementations of MediaHelper interface this interface is created.
     * It's intended for calls on ApplicationContext:
     * {@code ApplicationContextHolder.getContext().getBean(MediaHelperFactory.class).getMediaHelperInstance()}
     * </p>
     */
    @FunctionalInterface
    interface MediaHelperFactory {

        MediaHelper getMediaHelperInstance();
    }

    /**
     * Intended to help to build CD-version-specific, format-specific, CID-existence specific URL.
     */
    @FunctionalInterface
    interface ResponsiveMediaUrlBuilder {

        ResponsiveMediaUrlBuilder.Builder newInstance();

        @Setter
        @Getter
        @Accessors(chain = true)
        abstract class Builder {

            private String baseUrl;

            private String width;

            private String height;

            private boolean zeroAspect;

            public String build() {
                if (baseUrl == null || width == null || height == null) {
                    throw new NullPointerException("All basic (baseUrl, width, height) String parameters " +
                            "for ResponsiveMediaUrlBuilder should be not null!");
                }
                return buildInternal();
            }

            protected abstract String buildInternal();
        }
    }
}

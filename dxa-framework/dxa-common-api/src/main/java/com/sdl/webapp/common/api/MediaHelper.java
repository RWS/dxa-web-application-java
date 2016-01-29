package com.sdl.webapp.common.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

public interface MediaHelper {

    int getResponsiveWidth(String widthFactor, int containerSize);

    int getResponsiveHeight(String widthFactor, double aspect, int containerSize);

    String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize);

    int getGridSize();

    ScreenWidth getScreenWidth();

    int getSmallScreenBreakpoint();

    int getMediumScreenBreakpoint();

    int getLargeScreenBreakpoint();

    double getDefaultMediaAspect();

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
    interface MediaHelperFactory {
        MediaHelper getMediaHelperInstance();
    }

    /**
     * Intended to help to build CD-version-specific, format-specific, CID-existence specific URL.
     */
    abstract class ResponsiveMediaUrlBuilder {

        public abstract ResponsiveMediaUrlBuilder.Builder newInstance();

        public interface HostsNamesProvider {
            String getHostname();

            String getCidHostname();
        }

        @Component
        public static class StubHostsNamesProvider implements HostsNamesProvider {

            @Override
            public String getHostname() {
                throw new UnsupportedOperationException("Usage of stub implementation.");
            }

            @Override
            public String getCidHostname() {
                throw new UnsupportedOperationException("Usage of stub implementation.");
            }
        }

        @Setter
        @Getter
        @Accessors(chain = true)
        public static abstract class Builder {
            private String baseUrl, width, height;
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

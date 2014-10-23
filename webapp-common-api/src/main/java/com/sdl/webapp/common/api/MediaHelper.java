package com.sdl.webapp.common.api;

/**
 * Media helper.
 *
 * Implementation note: Implementations of {@code MediaHelper} should not be registered directly as Spring beans
 * (they must not have an {@code @Component} annotation). Creating an appropriate {@code MediaHelper} implementation
 * is done by {@link com.sdl.webapp.common.impl.MediaHelperFactory}.
 *
 * @see com.sdl.webapp.common.impl.MediaHelperFactory
 */
public interface MediaHelper {

    int getResponsiveWidth(String widthFactor, int containerSize);

    int getResponsiveHeight(String widthFactor, double aspect, int containerSize);

    String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize);

    int getGridSize();

    int getSmallScreenBreakpoint();

    int getMediumScreenBreakpoint();

    int getLargeScreenBreakpoint();

    double getDefaultMediaAspect();

    String getDefaultMediaFill();
}

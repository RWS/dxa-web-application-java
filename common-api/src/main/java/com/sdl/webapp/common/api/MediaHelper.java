package com.sdl.webapp.common.api;

/**
 * Media helper.
 */
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
}

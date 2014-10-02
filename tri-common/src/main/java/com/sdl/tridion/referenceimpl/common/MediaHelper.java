package com.sdl.tridion.referenceimpl.common;

/**
 * TODO: Documentation.
 */
public interface MediaHelper {

    int getResponsiveWidth(String widthFactor, int containerSize);

    int getResponsiveHeight(String widthFactor, double aspect, int containerSize);

    String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize);

    int getGridSize();

    double getDefaultMediaAspect();

    String getDefaultMediaFill();
}

package com.sdl.tridion.referenceimpl.common;

public interface MediaHelper {

    // TODO: See IMediaHelper (C#); implement ContextualMediaHelper

    int getResponsiveWidth(String widthFactor, int containerSize);

    int getResponsiveHeight(String widthFactor, double aspect, int containerSize);

    String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize);
}

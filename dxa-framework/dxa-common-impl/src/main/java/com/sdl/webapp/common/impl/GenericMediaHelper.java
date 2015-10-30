package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;

/**
 * Generic implementation of {@code MediaHelper}.
 */
public class GenericMediaHelper extends AbstractMediaHelper {

    public GenericMediaHelper(WebRequestContext webRequestContext) {
        super(webRequestContext);
    }

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        final int width = roundWidth(getResponsiveWidth(widthFactor, containerSize));

        // Height is calculated from the aspect ratio (0 means preserve aspect ratio)
        final String height = aspect == 0.0 ? "" : ("_h" + Integer.toString((int) Math.ceil(width / aspect)));

        final int index = url.lastIndexOf('.');
        final String baseUrl, extension;
        if (index >= 0 && index < url.length() - 1) {
            baseUrl = url.substring(0, index);
            extension = url.substring(index);
        } else {
            baseUrl = url;
            extension = "";
        }

        return String.format("%s_w%d%s_n%s", baseUrl, width, height, extension);
    }
}

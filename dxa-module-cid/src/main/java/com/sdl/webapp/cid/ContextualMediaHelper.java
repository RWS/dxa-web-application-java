package com.sdl.webapp.cid;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.impl.AbstractMediaHelper;
import com.sdl.webapp.common.impl.GenericMediaHelper;

/**
 * Contextual Media Helper.
 * Implementation of {@code MediaHelper} for when Contextual Image Delivery is used.
 */
public class ContextualMediaHelper extends AbstractMediaHelper {

    public ContextualMediaHelper(WebRequestContext webRequestContext) {
        super(webRequestContext);
    }

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        final int width = roundWidth(getResponsiveWidth(widthFactor, containerSize));

        // Height is calculated from the aspect ratio (0 means preserve aspect ratio)
        final String height = aspect == 0.0 ? "" : Integer.toString((int) Math.ceil(width / aspect));

        return String.format("/cid/scale/%dx%s/source/site%s", width, height, url);
    }
}

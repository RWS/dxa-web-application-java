package com.sdl.webapp.cid;

import com.sdl.webapp.common.impl.AbstractMediaHelper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@code MediaHelper} for when Contextual Image Delivery is used.
 */
@Component
@Primary // Makes this implementation take priority over DefaultMediaHelper
public class ContextualMediaHelper extends AbstractMediaHelper {

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        final int width = roundWidth(getResponsiveWidth(widthFactor, containerSize));

        // Height is calculated from the aspect ratio (0 means preserve aspect ratio)
        final String height = aspect == 0.0 ? "" : Integer.toString((int) Math.ceil(width / aspect));

        return String.format("/cid/scale/%dx%s/source/site%s", width, height, url);
    }
}

package com.sdl.tridion.referenceimpl.cid;

import com.sdl.tridion.referenceimpl.common.AbstractMediaHelper;
import org.springframework.stereotype.Component;

@Component("contextualMediaHelper")
public class ContextualMediaHelper extends AbstractMediaHelper {

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        final int width = roundWidth(getResponsiveWidth(widthFactor, containerSize));

        // Height is calculated from the aspect ratio (0 means preserve aspect ratio)
        final String height = aspect == 0.0 ? "" : Integer.toString((int) Math.ceil(width / aspect));

        return String.format("/cid/scale/%dx%s/source/site%s", width, height, url);
    }
}

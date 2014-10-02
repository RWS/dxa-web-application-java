package com.sdl.tridion.referenceimpl.cid;

import com.sdl.tridion.referenceimpl.common.BaseMediaHelper;
import org.springframework.stereotype.Component;

@Component("contextualMediaHelper")
public class ContextualMediaHelper extends BaseMediaHelper {

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        int width = getResponsiveWidth(widthFactor, containerSize);

        // Round the width to the nearest set limit point - important as we do not want to swamp the cache
        // with lots of different sized versions of the same image
        for (int i = 0; i < IMAGE_WIDTHS.length; i++) {
            if (width <= IMAGE_WIDTHS[i] || i == IMAGE_WIDTHS.length - 1) {
                width = IMAGE_WIDTHS[i];
                break;
            }
        }

        // Height is calculated from the aspect ratio (0 means preserve aspect ratio)
        final String height = aspect == 0.0 ? "" : Integer.toString((int) Math.ceil(width / aspect));

        return String.format("/cid/scale/%dx%s/source/site%s", width, height, url);
    }
}

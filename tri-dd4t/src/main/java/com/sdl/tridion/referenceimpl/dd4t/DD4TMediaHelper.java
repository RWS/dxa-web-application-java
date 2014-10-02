package com.sdl.tridion.referenceimpl.dd4t;

import com.sdl.tridion.referenceimpl.common.BaseMediaHelper;
import org.springframework.stereotype.Component;

@Component("dd4tMediaHelper")
public class DD4TMediaHelper extends BaseMediaHelper {

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        // TODO: Implement method
        throw new UnsupportedOperationException("DD4TMediaHelper.getResponsiveImageUrl() is not yet implemented");
    }
}

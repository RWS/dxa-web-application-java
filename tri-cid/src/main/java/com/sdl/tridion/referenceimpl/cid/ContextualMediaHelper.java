package com.sdl.tridion.referenceimpl.cid;

import com.sdl.tridion.referenceimpl.common.MediaHelper;
import org.springframework.stereotype.Component;

// TODO: Put this in a separate module? (For example tri-cid) so that you can leave it out if you don't have a license
// for CID (only that module should have dependencies on CWD libs)

@Component
public class ContextualMediaHelper implements MediaHelper {

    @Override
    public int getResponsiveWidth(String widthFactor, int containerSize) {
        // TODO: Implement method
        return 0;
    }

    @Override
    public int getResponsiveHeight(String widthFactor, double aspect, int containerSize) {
        // TODO: Implement method
        return 0;
    }

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        // TODO: Implement method
        return null;
    }
}

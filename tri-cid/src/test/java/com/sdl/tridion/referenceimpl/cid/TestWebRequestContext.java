package com.sdl.tridion.referenceimpl.cid;

import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import org.springframework.stereotype.Component;

@Component
public class TestWebRequestContext extends WebRequestContext {

    @Override
    public int getDisplayWidth() {
        return 1920;
    }

    @Override
    public double getPixelRatio() {
        return 1.0;
    }

    @Override
    public int getMaxMediaWidth() {
        return 2048;
    }
}

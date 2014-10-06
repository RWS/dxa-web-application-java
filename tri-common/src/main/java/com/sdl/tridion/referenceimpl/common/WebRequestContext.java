package com.sdl.tridion.referenceimpl.common;

import com.sdl.tridion.referenceimpl.common.config.ScreenWidth;
import com.tridion.ambientdata.web.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Component that provides information about the current client configuration, such as the size of the client's
 * screen, pixel ratio etc.
 */
@Component
public class WebRequestContext {

    private static final URI URI_BROWSER_DISPLAY_WIDTH = URI.create("taf:claim:context:browser:displayWidth");
    private static final URI URI_DEVICE_PIXEL_RATIO = URI.create("taf:claim:context:device:pixelRatio");

    private static final int MAX_WIDTH = 1024;

    @Autowired
    private ApplicationContext springContext;

    public ScreenWidth getScreenWidth() {
        final MediaHelper mediaHelper = BaseMediaHelper.getMediaHelper(springContext);

        final int displayWidth = getDisplayWidth();
        if (displayWidth < mediaHelper.getSmallScreenBreakpoint()) {
            return ScreenWidth.EXTRA_SMALL;
        } else if (displayWidth < mediaHelper.getMediumScreenBreakpoint()) {
            return ScreenWidth.SMALL;
        } else if (displayWidth < mediaHelper.getLargeScreenBreakpoint()) {
            return ScreenWidth.MEDIUM;
        } else {
            return ScreenWidth.LARGE;
        }
    }

    public int getDisplayWidth() {
        return (int) WebContext.getCurrentClaimStore().get(URI_BROWSER_DISPLAY_WIDTH);
    }

    public double getPixelRatio() {
        return (double) WebContext.getCurrentClaimStore().get(URI_DEVICE_PIXEL_RATIO);
    }

    public int getMaxMediaWidth() {
        return (int) (Math.max(1.0, getPixelRatio()) * Math.min(getDisplayWidth(), MAX_WIDTH));
    }
}

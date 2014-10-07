package com.sdl.tridion.referenceimpl.common.config;

import com.sdl.tridion.referenceimpl.common.MediaHelper;
import com.sdl.tridion.referenceimpl.common.MediaHelperProvider;
import com.tridion.ambientdata.web.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Component that provides information about the current client configuration, such as the size of the client's
 * screen, pixel ratio etc.
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebRequestContext {

    private static final URI URI_BROWSER_DISPLAY_WIDTH = URI.create("taf:claim:context:browser:displayWidth");
    private static final URI URI_DEVICE_PIXEL_RATIO = URI.create("taf:claim:context:device:pixelRatio");

    private static final int MAX_WIDTH = 1024;

    @Autowired
    private MediaHelperProvider mediaHelperProvider;

    private Localization localization;

    public ScreenWidth getScreenWidth() {
        final MediaHelper mediaHelper = mediaHelperProvider.getMediaHelper();

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

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}

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

    private ScreenWidth screenWidth;
    private Integer displayWidth;
    private Double pixelRatio;
    private Integer maxMediaWidth;

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public int getPublicationId() {
        return localization.getPublicationId();
    }

    public ScreenWidth getScreenWidth() {
        if (screenWidth == null) {
            final MediaHelper mediaHelper = mediaHelperProvider.getMediaHelper();

            final int displayWidth = getDisplayWidth();
            if (displayWidth < mediaHelper.getSmallScreenBreakpoint()) {
                screenWidth = ScreenWidth.EXTRA_SMALL;
            } else if (displayWidth < mediaHelper.getMediumScreenBreakpoint()) {
                screenWidth = ScreenWidth.SMALL;
            } else if (displayWidth < mediaHelper.getLargeScreenBreakpoint()) {
                screenWidth = ScreenWidth.MEDIUM;
            } else {
                screenWidth = ScreenWidth.LARGE;
            }
        }

        return screenWidth;
    }

    public int getDisplayWidth() {
        if (displayWidth == null) {
            displayWidth = (int) WebContext.getCurrentClaimStore().get(URI_BROWSER_DISPLAY_WIDTH);
        }
        return displayWidth;
    }

    public double getPixelRatio() {
        if (pixelRatio == null) {
            pixelRatio = (double) WebContext.getCurrentClaimStore().get(URI_DEVICE_PIXEL_RATIO);
        }
        return pixelRatio;
    }

    public int getMaxMediaWidth() {
        if (maxMediaWidth == null) {
            maxMediaWidth = (int) (Math.max(1.0, getPixelRatio()) * Math.min(getDisplayWidth(), MAX_WIDTH));
        }
        return maxMediaWidth;
    }
}

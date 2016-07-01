package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import lombok.Getter;

import java.util.List;

/**
 * ContextClaims with a 'browser' aspect
 */
public class BrowserClaims extends ContextClaims {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAspectName() {
        return "browser";
    }

    /**
     * Returns if cookies are supported.
     *
     * @return true if cookies are supported
     */
    @Getter(lazy = true)
    private final Boolean cookieSupport = cookieSupport();

    /**
     * Returns the color depth of display.
     *
     * @return a display color depth
     */
    @Getter(lazy = true)
    private final Integer displayColorDepth = displayColorDepth();

    /**
     * Returns the height of display.
     *
     * @return a display height
     */
    @Getter(lazy = true)
    private final Integer displayHeight = displayHeight();

    /**
     * Returns the width of display.
     *
     * @return a display width
     */
    @Getter(lazy = true)
    private final Integer displayWidth = displayWidth();

    /**
     * Returns the version of CSS supported.
     *
     * @return a CSS version
     */
    @Getter(lazy = true)
    private final String cssVersion = cssVersion();

    /**
     * Returns the version of JavaScript supported.
     *
     * @return a JavaScript version
     */
    @Getter(lazy = true)
    private final String jsVersion = jsVersion();

    /**
     * Returns the Browser Object Model.
     *
     * @return a Browser Object Model
     */
    @Getter(lazy = true)
    private final String model = model();

    /**
     * Returns the Preferred Html Content Type.
     *
     * @return a Preferred Html Content Type
     */
    @Getter(lazy = true)
    private final String preferredHtmlContentType = preferredHtmlContentType();

    /**
     * Returns the Browser Variant.
     *
     * @return a Browser Variant
     */
    @Getter(lazy = true)
    private final String variant = variant();

    /**
     * Returns the Browser Vendor.
     *
     * @return a Browser Vendor
     */
    @Getter(lazy = true)
    private final String vendor = vendor();

    /**
     * Returns the Browser Version.
     *
     * @return a Browser Version
     */
    @Getter(lazy = true)
    private final String version = version();

    /**
     * Returns the array of supported image formats.
     *
     * @return supported image formats
     */
    @Getter(lazy = true)
    private final String[] imageFormatSupport = imageFormatSupport();

    /**
     * Returns the array of input devices.
     *
     * @return input devices
     */
    @Getter(lazy = true)
    private final String[] inputDevices = inputDevices();

    /**
     * Returns the array of supported modes.
     *
     * @return supported modes
     */
    @Getter(lazy = true)
    private final String[] inputModeSupport = inputModeSupport();

    /**
     * Returns the array of supported markups.
     *
     * @return supported markups
     */
    @Getter(lazy = true)
    private final String[] markupSupport = markupSupport();

    /**
     * Returns the array of supported scripts.
     *
     * @return supported scripts
     */
    @Getter(lazy = true)
    private final String[] scriptSupport = scriptSupport();

    /**
     * Returns the array of supported stylesheets.
     *
     * @return supported stylesheets
     */
    @Getter(lazy = true)
    private final String[] stylesheetSupport = stylesheetSupport();

    private Boolean cookieSupport() {
        // BUG: Returns false on desktop FF, IE, Safari
        return getSingleClaim("cookieSupport", Boolean.class);
    }

    private String cssVersion() {

        return getSingleClaim("cssVersion", String.class);
    }

    private Integer displayColorDepth() {

        return getSingleClaim("displayColorDepth", Integer.class);
    }

    private Integer displayHeight() {

        return getSingleClaim("displayHeight", Integer.class);
    }

    private Integer displayWidth() {

        return getSingleClaim("displayWidth", Integer.class);
    }

    private String jsVersion() {

        return getSingleClaim("jsVersion", String.class);
    }

    private String model() {

        return getSingleClaim("model", String.class);
    }

    private String preferredHtmlContentType() {

        return getSingleClaim("preferredHtmlContentType", String.class);
    }

    private String variant() {

        return getSingleClaim("variant", String.class);
    }

    private String vendor() {

        return getSingleClaim("vendor", String.class);
    }

    private String version() {

        return getSingleClaim("version", String.class);
    }

    private String[] imageFormatSupport() {

        return getStringsClaims("imageFormatSupport");
    }

    private String[] inputDevices() {

        return getStringsClaims("inputDevices");
    }

    private String[] inputModeSupport() {

        return getStringsClaims("inputModeSupport");
    }

    private String[] markupSupport() {

        return getStringsClaims("markupSupport");
    }

    private String[] scriptSupport() {

        return getStringsClaims("scriptSupport");
    }

    private String[] stylesheetSupport() {

        return getStringsClaims("stylesheetSupport");
    }

    private String[] getStringsClaims(String name) {
        List<String> list = getClaimsList(name, String.class);
        return list.toArray(new String[list.size()]);
    }
}

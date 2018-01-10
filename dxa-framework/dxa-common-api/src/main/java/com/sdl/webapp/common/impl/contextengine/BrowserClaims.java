package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import lombok.Getter;

import java.util.List;

/**
 * ContextClaims with a 'browser' aspect.
 *
 * @dxa.publicApi
 */
public class BrowserClaims extends ContextClaims {

    private static final String ASPECT_NAME = "browser";

    @Getter(lazy = true)
    private final Boolean cookieSupport = cookieSupport();

    @Getter(lazy = true)
    private final Integer displayColorDepth = displayColorDepth();

    @Getter(lazy = true)
    private final Integer displayHeight = displayHeight();

    @Getter(lazy = true)
    private final Integer displayWidth = displayWidth();

    @Getter(lazy = true)
    private final String cssVersion = cssVersion();

    @Getter(lazy = true)
    private final String jsVersion = jsVersion();

    @Getter(lazy = true)
    private final String model = model();

    @Getter(lazy = true)
    private final String preferredHtmlContentType = preferredHtmlContentType();

    @Getter(lazy = true)
    private final String variant = variant();

    @Getter(lazy = true)
    private final String vendor = vendor();

    @Getter(lazy = true)
    private final String version = version();

    @Getter(lazy = true)
    private final String[] imageFormatSupport = imageFormatSupport();

    @Getter(lazy = true)
    private final String[] inputDevices = inputDevices();

    @Getter(lazy = true)
    private final String[] inputModeSupport = inputModeSupport();

    @Getter(lazy = true)
    private final String[] markupSupport = markupSupport();

    @Getter(lazy = true)
    private final String[] scriptSupport = scriptSupport();

    @Getter(lazy = true)
    private final String[] stylesheetSupport = stylesheetSupport();

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAspectName() {
        return ASPECT_NAME;
    }

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

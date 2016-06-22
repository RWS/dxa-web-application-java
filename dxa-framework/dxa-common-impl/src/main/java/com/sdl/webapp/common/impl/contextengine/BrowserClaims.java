package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;

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
     * @return true is cookies are supported
     */
    public Boolean getCookieSupport() {
        // BUG: Returns false on desktop FF, IE, Safari
        return getSingleClaim("cookieSupport", Boolean.class);
    }

    /**
     * Returns the version of CSS supported.
     *
     * @return a CSS version
     */
    public String getCssVersion() {
        return getSingleClaim("cssVersion", String.class);
    }

    public Integer getDisplayColorDepth() {
        return getSingleClaim("displayColorDepth", Integer.class);
    }

    public Integer getDisplayHeight() {
        return getSingleClaim("displayHeight", Integer.class);
    }

    public Integer getDisplayWidth() {
        return getSingleClaim("displayWidth", Integer.class);
    }

    public String[] getImageFormatSupport() {
        return getStringsClaims("imageFormatSupport");
    }

    public String[] getInputDevices() {
        return getStringsClaims("inputDevices");
    }

    public String[] getInputModeSupport() {
        return getStringsClaims("inputModeSupport");
    }

    public String getJsVersion() {
        return getSingleClaim("jsVersion", String.class);
    }

    public String[] getMarkupSupport() {
        return getStringsClaims("markupSupport");
    }


    public String getModel() {
        return getSingleClaim("model", String.class);
    }

    public String getPreferredHtmlContentType() {
        return getSingleClaim("preferredHtmlContentType", String.class);
    }

    public String[] getScriptSupport() {
        return getStringsClaims("scriptSupport");
    }

    public String[] getStylesheetSupport() {
        return getStringsClaims("stylesheetSupport");
    }

    public String getVariant() {
        return getSingleClaim("variant", String.class);
    }

    public String getVendor() {
        return getSingleClaim("vendor", String.class);
    }

    public String getVersion() {
        return getSingleClaim("version", String.class);
    }

    private String[] getStringsClaims(String name) {
        List<String> list = getClaimsList(name, String.class);
        return list.toArray(new String[list.size()]);
    }
}

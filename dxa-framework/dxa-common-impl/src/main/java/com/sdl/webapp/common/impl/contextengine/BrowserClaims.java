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
        List<String> imageFormatSupport = getClaimsList("imageFormatSupport", String.class);
        return imageFormatSupport == null ? new String[]{} : imageFormatSupport.toArray(new String[imageFormatSupport.size()]);
    }

    public String[] getInputDevices() {
        List<String> inputDevices = getClaimsList("inputDevices", String.class);
        return inputDevices == null ? new String[]{} : inputDevices.toArray(new String[inputDevices.size()]);
    }

    public String[] getInputModeSupport() {
        List<String> inputModeSupport = getClaimsList("inputModeSupport", String.class);
        return inputModeSupport == null ? new String[]{} : inputModeSupport.toArray(new String[inputModeSupport.size()]);
    }

    public String getJsVersion() {
        return getSingleClaim("jsVersion", String.class);
    }

    public String[] getMarkupSupport() {
        List<String> markupSupport = getClaimsList("markupSupport", String.class);
        return markupSupport == null ? new String[]{} : markupSupport.toArray(new String[markupSupport.size()]);
    }

    public String getModel() {
        return getSingleClaim("model", String.class);
    }

    public String getPreferredHtmlContentType() {
        return getSingleClaim("preferredHtmlContentType", String.class);
    }

    public String[] getScriptSupport() {
        List<String> scriptSupport = getClaimsList("scriptSupport", String.class);
        return scriptSupport == null ? new String[]{} : scriptSupport.toArray(new String[scriptSupport.size()]);
    }

    public String[] getStylesheetSupport() {
        List<String> stylesheetSupport = getClaimsList("stylesheetSupport", String.class);
        return stylesheetSupport == null ? new String[]{} : stylesheetSupport.toArray(new String[stylesheetSupport.size()]);
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
}

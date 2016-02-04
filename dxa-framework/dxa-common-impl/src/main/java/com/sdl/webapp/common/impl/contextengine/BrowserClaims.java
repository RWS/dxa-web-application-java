package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;

import java.util.List;


/**
 * <p>BrowserClaims class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
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
     * <p>getCookieSupport.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getCookieSupport() {
        // BUG: Returns false on desktop FF, IE, Safari
        return getClaimValue("cookieSupport", Boolean.class);
    }

    /**
     * <p>getCssVersion.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCssVersion() {
        return getClaimValue("cssVersion", String.class);
    }

    /**
     * <p>getDisplayColorDepth.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getDisplayColorDepth() {
        return getClaimValue("displayColorDepth", Integer.class);
    }

    /**
     * <p>getDisplayHeight.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getDisplayHeight() {
        return getClaimValue("displayHeight", Integer.class);
    }

    /**
     * <p>getDisplayWidth.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getDisplayWidth() {
        return getClaimValue("displayWidth", Integer.class);
    }

    /**
     * <p>getImageFormatSupport.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getImageFormatSupport() {
        List<String> imageFormatSupport = getClaimValues("imageFormatSupport", String.class);
        return imageFormatSupport.toArray(new String[imageFormatSupport.size()]);
    }

    /**
     * <p>getInputDevices.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getInputDevices() {
        List<String> inputDevices = getClaimValues("inputDevices", String.class);
        return inputDevices.toArray(new String[inputDevices.size()]);
    }

    /**
     * <p>getInputModeSupport.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getInputModeSupport() {
        List<String> inputModeSupport = getClaimValues("inputModeSupport", String.class);
        return inputModeSupport.toArray(new String[inputModeSupport.size()]);
    }

    /**
     * <p>getJsVersion.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsVersion() {
        return getClaimValue("jsVersion", String.class);
    }

    /**
     * <p>getMarkupSupport.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getMarkupSupport() {
        List<String> markupSupport = getClaimValues("markupSupport", String.class);
        return markupSupport.toArray(new String[markupSupport.size()]);
    }

    /**
     * <p>getModel.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getModel() {
        return getClaimValue("model", String.class);
    }

    /**
     * <p>getPreferredHtmlContentType.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPreferredHtmlContentType() {
        return getClaimValue("preferredHtmlContentType", String.class);
    }

    /**
     * <p>getScriptSupport.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getScriptSupport() {
        List<String> scriptSupport = getClaimValues("scriptSupport", String.class);
        return scriptSupport.toArray(new String[scriptSupport.size()]);
    }

    /**
     * <p>getStylesheetSupport.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getStylesheetSupport() {
        List<String> stylesheetSupport = getClaimValues("stylesheetSupport", String.class);
        return stylesheetSupport.toArray(new String[stylesheetSupport.size()]);
    }

    /**
     * <p>getVariant.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVariant() {
        return getClaimValue("variant", String.class);
    }

    /**
     * <p>getVendor.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVendor() {
        return getClaimValue("vendor", String.class);
    }

    /**
     * <p>getVersion.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVersion() {
        return getClaimValue("version", String.class);
    }
}

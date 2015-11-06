package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;


public class BrowserClaims extends ContextClaims {

    @Override
    protected String getAspectName() {
        return "browser";
    }

    public Boolean getCookieSupport() {
        // BUG: Returns false on desktop FF, IE, Safari
        return getClaimValue("cookieSupport", Boolean.class);
    }

    public String getCssVersion() {
        return getClaimValue("cssVersion", String.class);
    }

    public Integer getDisplayColorDepth() {
        return getClaimValue("displayColorDepth", Integer.class);
    }

    public Integer getDisplayHeight() {
        return getClaimValue("displayHeight", Integer.class);
    }

    public Integer getDisplayWidth() {
        return getClaimValue("displayWidth", Integer.class);
    }

    public String[] getImageFormatSupport() {
        return getClaimValues("imageFormatSupport", String.class).toArray(new String[0]);
    }

    public String[] getInputDevices() {
        return getClaimValues("inputDevices", String.class).toArray(new String[0]);
    }

    public String[] getInputModeSupport() {
        return getClaimValues("inputModeSupport", String.class).toArray(new String[0]);
    }

    public String getJsVersion() {
        return getClaimValue("jsVersion", String.class);
    }

    public String[] getMarkupSupport() {
        return getClaimValues("markupSupport", String.class).toArray(new String[0]);
    }

    public String getModel() {
        return getClaimValue("model", String.class);
    }

    public String getPreferredHtmlContentType() {
        return getClaimValue("preferredHtmlContentType", String.class);
    }

    public String[] getScriptSupport() {
        return getClaimValues("scriptSupport", String.class).toArray(new String[0]);
    }

    public String[] getStylesheetSupport() {
        return getClaimValues("stylesheetSupport", String.class).toArray(new String[0]);
    }

    public String getVariant() {
        return getClaimValue("variant", String.class);
    }

    public String getVendor() {
        return getClaimValue("vendor", String.class);
    }

    public String getVersion() {
        return getClaimValue("version", String.class);
    }
}

package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;


public class OperatingSystemClaims extends ContextClaims {

    @Override
    protected String getAspectName() {
        return "os";
    }

    public String getModel() {
        return getClaimValue("model", String.class);
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

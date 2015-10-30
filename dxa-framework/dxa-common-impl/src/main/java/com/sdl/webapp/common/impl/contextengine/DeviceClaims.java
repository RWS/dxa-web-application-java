package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;


public class DeviceClaims extends ContextClaims {

    @Override
    protected String getAspectName() {
        return "device";
    }


    public Integer getDisplayHeight() {
        return getClaimValue("displayHeight", Integer.class);
    }

    public Integer getDisplayWidth() {
        return getClaimValue("displayWidth", Integer.class);
    }

    public Boolean getIsMobile() {
        return getClaimValue("mobile", Boolean.class);
    }

    public String getModel() {
        return getClaimValue("model", String.class);
    }

    public Integer getPixelDensity() {
        return getClaimValue("pixelDensity", Integer.class);
    }

    public Double getPixelRatio() {
        return getClaimValue("pixelRatio", Double.class);
    }

    public Boolean getIsRobot() {
        return getClaimValue("robot", Boolean.class);
    }

    public Boolean getIsTablet() {
        return getClaimValue("tablet", Boolean.class);
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

package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;


/**
 * ContextClaims with a 'device' aspect
 */
public class DeviceClaims extends ContextClaims {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAspectName() {
        return "device";
    }

    public Integer getDisplayHeight() {
        return getSingleClaim("displayHeight", Integer.class);
    }

    public Integer getDisplayWidth() {
        return getSingleClaim("displayWidth", Integer.class);
    }

    public Boolean getIsMobile() {
        return getSingleClaim("mobile", Boolean.class);
    }

    public String getModel() {
        return getSingleClaim("model", String.class);
    }

    public Integer getPixelDensity() {
        return getSingleClaim("pixelDensity", Integer.class);
    }

    public Double getPixelRatio() {
        return getSingleClaim("pixelRatio", Double.class);
    }

    public Boolean getIsRobot() {
        return getSingleClaim("robot", Boolean.class);
    }

    public Boolean getIsTablet() {
        return getSingleClaim("tablet", Boolean.class);
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

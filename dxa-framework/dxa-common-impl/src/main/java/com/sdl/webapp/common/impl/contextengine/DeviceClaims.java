package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;


/**
 * <p>DeviceClaims class.</p>
 */
public class DeviceClaims extends ContextClaims {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAspectName() {
        return "device";
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
     * <p>getIsMobile.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsMobile() {
        return getClaimValue("mobile", Boolean.class);
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
     * <p>getPixelDensity.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getPixelDensity() {
        return getClaimValue("pixelDensity", Integer.class);
    }

    /**
     * <p>getPixelRatio.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getPixelRatio() {
        return getClaimValue("pixelRatio", Double.class);
    }

    /**
     * <p>getIsRobot.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsRobot() {
        return getClaimValue("robot", Boolean.class);
    }

    /**
     * <p>getIsTablet.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsTablet() {
        return getClaimValue("tablet", Boolean.class);
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

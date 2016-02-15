package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;


/**
 * <p>OperatingSystemClaims class.</p>
 */
public class OperatingSystemClaims extends ContextClaims {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAspectName() {
        return "os";
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

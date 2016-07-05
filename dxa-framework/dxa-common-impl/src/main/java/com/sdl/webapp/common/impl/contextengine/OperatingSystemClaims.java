package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;


/**
 * ContextClaims with a 'os' aspect
 */
public class OperatingSystemClaims extends ContextClaims {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAspectName() {
        return "os";
    }

    public String getModel() {
        return getSingleClaim("model", String.class);
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

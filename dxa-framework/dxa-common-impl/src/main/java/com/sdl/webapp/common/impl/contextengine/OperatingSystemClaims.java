package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import lombok.Getter;


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

    /**
     * Returns the operating system model.
     *
     * @return an operating system model
     */
    @Getter(lazy = true)
    private final String model = model();

    /**
     * Returns the operating system variant.
     *
     * @return an operating system variant
     */
    @Getter(lazy = true)
    private final String variant = variant();

    /**
     * Returns the operating system vendor.
     *
     * @return an operating system vendor
     */
    @Getter(lazy = true)
    private final String vendor = vendor();

    /**
     * Returns the operating system version.
     *
     * @return an operating system version
     */
    @Getter(lazy = true)
    private final String version = version();

    private String model() {

        return getSingleClaim("model", String.class);
    }

    private String variant() {

        return getSingleClaim("variant", String.class);
    }

    private String vendor() {

        return getSingleClaim("vendor", String.class);
    }

    private String version() {

        return getSingleClaim("version", String.class);
    }
}

package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import lombok.Getter;


/**
 * ContextClaims with a 'os' aspect.
 *
 * @dxa.publicApi
 */
public class OperatingSystemClaims extends ContextClaims {

    private static final String ASPECT_NAME = "os";

    @Getter(lazy = true)
    private final String model = model();

    @Getter(lazy = true)
    private final String variant = variant();

    @Getter(lazy = true)
    private final String vendor = vendor();

    @Getter(lazy = true)
    private final String version = version();

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAspectName() {
        return ASPECT_NAME;
    }

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

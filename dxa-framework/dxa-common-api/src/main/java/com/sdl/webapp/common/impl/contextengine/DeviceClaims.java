package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import lombok.Getter;


/**
 * ContextClaims with a 'device' aspect.
 *
 * @dxa.publicApi
 */
public class DeviceClaims extends ContextClaims {

    private static final String ASPECT_NAME = "device";

    @Getter(lazy = true)
    private final Boolean isMobile = isMobile();

    @Getter(lazy = true)
    private final Boolean isRobot = isRobot();

    @Getter(lazy = true)
    private final Boolean isTablet = isTablet();

    @Getter(lazy = true)
    private final Integer displayHeight = displayHeight();

    @Getter(lazy = true)
    private final Integer displayWidth = displayWidth();

    @Getter(lazy = true)
    private final Integer pixelDensity = pixelDensity();

    @Getter(lazy = true)
    private final Double pixelRatio = pixelRatio();

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

    private Boolean isMobile() {

        return getSingleClaim("mobile", Boolean.class);
    }

    private Boolean isRobot() {

        return getSingleClaim("robot", Boolean.class);
    }

    private Boolean isTablet() {

        return getSingleClaim("tablet", Boolean.class);
    }

    private Integer displayHeight() {

        return getSingleClaim("displayHeight", Integer.class);
    }

    private Integer displayWidth() {

        return getSingleClaim("displayWidth", Integer.class);
    }

    private Integer pixelDensity() {

        return getSingleClaim("pixelDensity", Integer.class);
    }

    private Double pixelRatio() {

        return getSingleClaim("pixelRatio", Double.class);
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

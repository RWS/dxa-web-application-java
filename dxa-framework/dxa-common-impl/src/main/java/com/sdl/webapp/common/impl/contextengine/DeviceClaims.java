package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import lombok.Getter;


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

    /**
     * Returns if device is mobile.
     *
     * @return true if device is mobile
     */
    @Getter(lazy = true)
    private final Boolean isMobile = isMobile();

    /**
     * Returns if device is robot.
     *
     * @return true if device is robot
     */
    @Getter(lazy = true)
    private final Boolean isRobot = isRobot();

    /**
     * Returns if device is tablet.
     *
     * @return true if device is tablet
     */
    @Getter(lazy = true)
    private final Boolean isTablet = isTablet();

    /**
     * Returns the height of display of device.
     *
     * @return a device display height
     */
    @Getter(lazy = true)
    private final Integer displayHeight = displayHeight();

    /**
     * Returns the width of display of device.
     *
     * @return a device display width
     */
    @Getter(lazy = true)
    private final Integer displayWidth = displayWidth();

    /**
     * Returns the pixelDensity of display of device.
     *
     * @return a device display pixelDensity
     */
    @Getter(lazy = true)
    private final Integer pixelDensity = pixelDensity();

    /**
     * Returns the pixelRatio of display of device.
     *
     * @return a device display pixelRatio
     */
    @Getter(lazy = true)
    private final Double pixelRatio = pixelRatio();

    /**
     * Returns the operating system model of device.
     *
     * @return an operating system model of device
     */
    @Getter(lazy = true)
    private final String model = model();

    /**
     * Returns the operating system variant of device.
     *
     * @return an operating system variant of device
     */
    @Getter(lazy = true)
    private final String variant = variant();

    /**
     * Returns the operating system vendor of device.
     *
     * @return an operating system vendor of device
     */
    @Getter(lazy = true)
    private final String vendor = vendor();

    /**
     * Returns the operating system version of device.
     *
     * @return an operating system version of device
     */
    @Getter(lazy = true)
    private final String version = version();

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

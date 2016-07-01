package com.sdl.webapp.common.impl.contextengine;

import org.junit.Test;

import static com.sdl.webapp.common.impl.contextengine.Claims.getClaims;
import static org.junit.Assert.*;

public class DeviceClaimsTest {

    @Test
    public void shouldReturnDevice() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();

        //when
        String aspectName = deviceClaims.getAspectName();

        //then
        assertEquals("device", aspectName);
    }

    @Test
    public void shouldReturnIsMobile() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.mobile", true));

        //when
        Boolean isMobile = deviceClaims.getIsMobile();

        //then
        assertTrue(isMobile);
    }

    @Test
    public void shouldReturnIsRobot() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.robot", true));

        //when
        Boolean isRobot = deviceClaims.getIsRobot();

        //then
        assertTrue(isRobot);
    }

    @Test
    public void shouldReturnIsTablet() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.tablet", true));

        //when
        Boolean isTablet = deviceClaims.getIsTablet();

        //then
        assertTrue(isTablet);
    }

    @Test
    public void shouldReturnDisplayHeight() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.displayHeight", 728));

        //when
        int displayHeight = deviceClaims.getDisplayHeight();

        //then
        assertEquals(728, displayHeight);
    }

    @Test
    public void shouldReturnDisplayWidth() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.displayWidth", 1024));

        //when
        int displayWidth = deviceClaims.getDisplayWidth();

        //then
        assertEquals(1024, displayWidth);
    }

    @Test
    public void shouldReturnPixelDensity() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.pixelDensity", 65536));

        //when
        int pixelDensity = deviceClaims.getPixelDensity();

        //then
        assertEquals(65536, pixelDensity);
    }

    @Test
    public void shouldReturnPixelRatio() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.pixelRatio", 2.0));

        //when
        double pixelRatio = deviceClaims.getPixelRatio();

        //then
        assertEquals(2.0, pixelRatio, 0.1);
    }

    @Test
    public void shouldReturnModel() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.model", "Linux"));

        //when
        String model = deviceClaims.getModel();

        //then
        assertEquals("Linux", model);
    }

    @Test
    public void shouldReturnVendor() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.vendor", "The Open Group"));

        //when
        String vendor = deviceClaims.getVendor();

        //then
        assertEquals("The Open Group", vendor);
    }

    @Test
    public void shouldReturnVariant() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.variant", "FreeBSD"));

        //when
        String variant = deviceClaims.getVariant();

        //then
        assertEquals("FreeBSD", variant);
    }

    @Test
    public void shouldReturnVersion() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(getClaims("device.version", "4.1.1"));

        //when
        String version = deviceClaims.getVersion();

        //then
        assertEquals("4.1.1", version);
    }

}
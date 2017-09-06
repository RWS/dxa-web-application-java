package com.sdl.webapp.common.impl.contextengine;

import org.junit.Test;

import static com.sdl.webapp.common.impl.contextengine.Claims.getClaims;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        int expected = 728;
        deviceClaims.setClaims(getClaims("device.displayHeight", expected));

        //when
        int displayHeight = deviceClaims.getDisplayHeight();

        //then
        assertEquals(expected, displayHeight);
    }

    @Test
    public void shouldReturnDisplayWidth() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        int expected = 1024;
        deviceClaims.setClaims(getClaims("device.displayWidth", expected));

        //when
        int displayWidth = deviceClaims.getDisplayWidth();

        //then
        assertEquals(expected, displayWidth);
    }

    @Test
    public void shouldReturnPixelDensity() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        int expected = 65536;
        deviceClaims.setClaims(getClaims("device.pixelDensity", expected));

        //when
        int pixelDensity = deviceClaims.getPixelDensity();

        //then
        assertEquals(expected, pixelDensity);
    }

    @Test
    public void shouldReturnPixelRatio() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        double expected = 2.0;
        deviceClaims.setClaims(getClaims("device.pixelRatio", expected));

        //when
        double pixelRatio = deviceClaims.getPixelRatio();

        //then
        assertEquals(expected, pixelRatio, 0.1);
    }

    @Test
    public void shouldReturnModel() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        String expected = "Linux";
        deviceClaims.setClaims(getClaims("device.model", expected));

        //when
        String model = deviceClaims.getModel();

        //then
        assertEquals(expected, model);
    }

    @Test
    public void shouldReturnVendor() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        String expected = "The Open Group";
        deviceClaims.setClaims(getClaims("device.vendor", expected));

        //when
        String vendor = deviceClaims.getVendor();

        //then
        assertEquals(expected, vendor);
    }

    @Test
    public void shouldReturnVariant() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        String expected = "FreeBSD";
        deviceClaims.setClaims(getClaims("device.variant", expected));

        //when
        String variant = deviceClaims.getVariant();

        //then
        assertEquals(expected, variant);
    }

    @Test
    public void shouldReturnVersion() {
        //given
        DeviceClaims deviceClaims = new DeviceClaims();
        String expected = "4.1.1";
        deviceClaims.setClaims(getClaims("device.version", expected));

        //when
        String version = deviceClaims.getVersion();

        //then
        assertEquals(expected, version);
    }

}
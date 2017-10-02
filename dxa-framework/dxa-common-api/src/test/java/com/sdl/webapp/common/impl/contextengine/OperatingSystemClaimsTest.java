package com.sdl.webapp.common.impl.contextengine;

import org.junit.Test;

import static com.sdl.webapp.common.impl.contextengine.Claims.getClaims;
import static org.junit.Assert.assertEquals;

public class OperatingSystemClaimsTest {

    @Test
    public void shouldReturnAspectName() {
        //given
        OperatingSystemClaims operatingSystemClaims = new OperatingSystemClaims();

        //when
        String aspectName = operatingSystemClaims.getAspectName();

        //then
        assertEquals("os", aspectName);
    }

    @Test
    public void shouldReturnModel() {
        //given
        OperatingSystemClaims operatingSystemClaims = new OperatingSystemClaims();
        String expected = "Linux";
        operatingSystemClaims.setClaims(getClaims("os.model", expected));

        //when
        String model = operatingSystemClaims.getModel();

        //then
        assertEquals(expected, model);
    }

    @Test
    public void shouldReturnVendor() {
        //given
        OperatingSystemClaims operatingSystemClaims = new OperatingSystemClaims();
        String expected = "The Open Group";
        operatingSystemClaims.setClaims(getClaims("os.vendor", expected));

        //when
        String vendor = operatingSystemClaims.getVendor();

        //then
        assertEquals(expected, vendor);
    }

    @Test
    public void shouldReturnVariant() {
        //given
        OperatingSystemClaims operatingSystemClaims = new OperatingSystemClaims();
        String expected = "FreeBSD";
        operatingSystemClaims.setClaims(getClaims("os.variant", expected));

        //when
        String variant = operatingSystemClaims.getVariant();

        //then
        assertEquals(expected, variant);
    }

    @Test
    public void shouldReturnVersion() {
        //given
        OperatingSystemClaims operatingSystemClaims = new OperatingSystemClaims();
        String expected = "4.1.1";
        operatingSystemClaims.setClaims(getClaims("os.version", expected));

        //when
        String version = operatingSystemClaims.getVersion();

        //then
        assertEquals(expected, version);
    }

}
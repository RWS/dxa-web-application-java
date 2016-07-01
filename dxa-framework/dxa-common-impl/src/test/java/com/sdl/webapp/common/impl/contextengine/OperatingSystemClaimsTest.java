package com.sdl.webapp.common.impl.contextengine;

import org.junit.Test;

import static com.sdl.webapp.common.impl.contextengine.Claims.getClaims;
import static org.junit.Assert.*;

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
        operatingSystemClaims.setClaims(getClaims("os.model", "Linux"));

        //when
        String model = operatingSystemClaims.getModel();

        //then
        assertEquals("Linux", model);
    }

    @Test
    public void shouldReturnVendor() {
        //given
        OperatingSystemClaims operatingSystemClaims = new OperatingSystemClaims();
        operatingSystemClaims.setClaims(getClaims("os.vendor", "The Open Group"));

        //when
        String vendor = operatingSystemClaims.getVendor();

        //then
        assertEquals("The Open Group", vendor);
    }

    @Test
    public void shouldReturnVariant() {
        //given
        OperatingSystemClaims operatingSystemClaims = new OperatingSystemClaims();
        operatingSystemClaims.setClaims(getClaims("os.variant", "FreeBSD"));

        //when
        String variant = operatingSystemClaims.getVariant();

        //then
        assertEquals("FreeBSD", variant);
    }

    @Test
    public void shouldReturnVersion() {
        //given
        OperatingSystemClaims operatingSystemClaims = new OperatingSystemClaims();
        operatingSystemClaims.setClaims(getClaims("os.version", "4.1.1"));

        //when
        String version = operatingSystemClaims.getVersion();

        //then
        assertEquals("4.1.1", version);
    }

}
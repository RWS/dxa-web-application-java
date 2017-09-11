package com.sdl.webapp.tridion.contextengine;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class AdfContextClaimsProviderTest {

    @Test
    public void shouldReturnNullAsDeviceFamily() throws Exception {
        //given
        AdfContextClaimsProvider provider = new AdfContextClaimsProvider();

        //when
        String deviceFamily = provider.getDeviceFamily();

        //then
        assertNull(deviceFamily);
    }
}
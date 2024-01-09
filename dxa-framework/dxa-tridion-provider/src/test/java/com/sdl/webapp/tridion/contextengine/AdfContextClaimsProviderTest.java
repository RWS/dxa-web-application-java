package com.sdl.webapp.tridion.contextengine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

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
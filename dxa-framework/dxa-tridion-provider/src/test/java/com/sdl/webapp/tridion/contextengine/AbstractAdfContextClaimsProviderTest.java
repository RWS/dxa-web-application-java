package com.sdl.webapp.tridion.contextengine;

import org.junit.Test;

import java.net.URI;
import java.util.Map;

import static org.junit.Assert.assertNull;

public class AbstractAdfContextClaimsProviderTest {

    @Test
    public void shouldReturnNullAsDeviceFamily() throws Exception {
        //given
        AbstractAdfContextClaimsProvider provider = new AbstractAdfContextClaimsProvider() {
            @Override
            protected Map<URI, Object> getCurrentClaims() {
                return null;
            }
        };

        //when
        String deviceFamily = provider.getDeviceFamily();

        //then
        assertNull(deviceFamily);
    }
}
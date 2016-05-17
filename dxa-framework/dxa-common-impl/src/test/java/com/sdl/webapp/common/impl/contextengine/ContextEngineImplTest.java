package com.sdl.webapp.common.impl.contextengine;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContextEngineImplTest {

    @Test
    public void shouldDefineFeaturePhone() throws DxaException {
        //given
        ContextClaimsProvider claimsProvider = mock(ContextClaimsProvider.class);
        ContextEngineImpl contextEngine = contextEngineImpl(claimsProvider);

        //when
        when(claimsProvider.getContextClaims(isNull(String.class))).thenReturn(featurePhoneClaims());

        //then
        assertEquals("featurephone", contextEngine.getDeviceFamily());
    }

    @Test
    public void shouldDefineSmartPhone() throws DxaException {
        //given
        ContextClaimsProvider claimsProvider = mock(ContextClaimsProvider.class);
        ContextEngineImpl contextEngine = contextEngineImpl(claimsProvider);

        //when
        when(claimsProvider.getContextClaims(isNull(String.class))).thenReturn(smartPhoneClaims());

        //then
        assertEquals("smartphone", contextEngine.getDeviceFamily());
    }

    @Test
    public void shouldDefineTablet() throws DxaException {
        //given
        ContextClaimsProvider claimsProvider = mock(ContextClaimsProvider.class);
        ContextEngineImpl contextEngine = contextEngineImpl(claimsProvider);

        //when
        when(claimsProvider.getContextClaims(isNull(String.class))).thenReturn(tabletClaims());

        //then
        assertEquals("tablet", contextEngine.getDeviceFamily());
    }

    @Test
    public void shouldDefineDesktop() throws DxaException {
        //given
        ContextClaimsProvider claimsProvider = mock(ContextClaimsProvider.class);
        ContextEngineImpl contextEngine = contextEngineImpl(claimsProvider);

        //when
        when(claimsProvider.getContextClaims(isNull(String.class))).thenReturn(desktopClaims());

        //then
        assertEquals("desktop", contextEngine.getDeviceFamily());
    }

    @Test
    public void shouldDefineApple() throws DxaException {
        //given
        ContextClaimsProvider claimsProvider = mock(ContextClaimsProvider.class);
        ContextEngineImpl contextEngine = contextEngineImpl(claimsProvider);

        //when
        when(claimsProvider.getContextClaims(isNull(String.class))).thenReturn(appleClaims());

        //then
        assertEquals("apple", contextEngine.getDeviceFamily());
    }

    @Test
    public void shouldDefineAlienDeviceThatProvesWeAreNotGettingFallback() throws DxaException {
        //given
        ContextClaimsProvider claimsProvider = mock(ContextClaimsProvider.class);
        ContextEngineImpl contextEngine = contextEngineImpl(claimsProvider);

        //when
        when(claimsProvider.getContextClaims(isNull(String.class))).thenReturn(alienDeviceClaims());

        //then
        assertEquals("aliendevice", contextEngine.getDeviceFamily());
    }

    private Map<String, Object> desktopClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.mobile", false)
                .put("device.tablet", false)
                .build();
    }

    private Map<String, Object> tabletClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.tablet", true)
                .build();
    }

    private Map<String, Object> smartPhoneClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.mobile", true)
                .put("device.tablet", false)
                .put("device.displayWidth", 330)
                .build();
    }

    private Map<String, Object> alienDeviceClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.mobile", false)
                .put("device.tablet", false)
                .put("device.displayWidth", 300)
                .build();
    }

    private Map<String, Object> appleClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("os.vendor", "Apple")
                .build();
    }

    private ImmutableMap<String, Object> featurePhoneClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.mobile", true)
                .put("device.tablet", false)
                .put("device.displayWidth", 300)
                .build();
    }

    private ContextEngineImpl contextEngineImpl(ContextClaimsProvider contextClaimsProvider) {
        ContextEngineImpl contextEngine = new ContextEngineImpl();
        ReflectionTestUtils.setField(contextEngine, "deviceFamiliesFile", "device-families.xml");
        ReflectionTestUtils.setField(contextEngine, "provider", contextClaimsProvider);
        ReflectionTestUtils.invokeMethod(contextEngine, "init");
        return contextEngine;
    }
}
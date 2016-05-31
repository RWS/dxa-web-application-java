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
        shouldResolveDeviceFamily("featurephone", featurePhoneClaims());
    }

    @Test
    public void shouldDefineSmartPhone() throws DxaException {
        shouldResolveDeviceFamily("smartphone", smartPhoneClaims());
    }

    @Test
    public void shouldDefineTablet() throws DxaException {
        shouldResolveDeviceFamily("tablet", tabletClaims());
    }

    @Test
    public void shouldDefineDesktop() throws DxaException {
        shouldResolveDeviceFamily("desktop", desktopClaims());
    }

    @Test
    public void shouldDefineApple() throws DxaException {
        shouldResolveDeviceFamily("apple", appleClaims());
    }

    @Test
    public void shouldDefineAlienDeviceThatProvesWeAreNotGettingFallback() throws DxaException {
        shouldResolveDeviceFamily("aliendevice", alienDeviceClaims());
    }

    @Test
    public void shouldFallbackToTabletDefault() throws DxaException {
        shouldFallbackDeviceFamily("desktop", desktopClaims());
    }

    @Test
    public void shouldFallbackToSmartPhoneDefault() throws DxaException {
        shouldFallbackDeviceFamily("smartphone", smartPhoneClaims());
    }

    @Test
    public void shouldFallbackToFeaturePhoneDefault() throws DxaException {
        shouldFallbackDeviceFamily("featurephone", featurePhoneClaims());
    }

    @Test
    public void shouldFallbackToDesktopDefault() throws DxaException {
        shouldFallbackDeviceFamily("desktop", desktopClaims());
    }

    @Test
    public void shouldFallbackToDesktopIfNoMatchDefault() throws DxaException {
        shouldFallbackDeviceFamily("desktop", alienDeviceClaims());
    }

    private void shouldResolveDeviceFamily(String deviceFamily, Map<String, Object> claims) throws DxaException {
        shouldResolveDeviceFamily(deviceFamily, claims, false);
    }

    private void shouldFallbackDeviceFamily(String deviceFamily, Map<String, Object> claims) throws DxaException {
        shouldResolveDeviceFamily(deviceFamily, claims, true);
    }

    private void shouldResolveDeviceFamily(String deviceFamily, Map<String, Object> claims, boolean isFallbackScenario) throws DxaException {
        //given
        ContextClaimsProvider claimsProvider = mock(ContextClaimsProvider.class);
        ContextEngineImpl contextEngine = contextEngineImpl(claimsProvider, !isFallbackScenario);

        //when
        when(claimsProvider.getContextClaims(isNull(String.class))).thenReturn(claims);

        //then
        assertEquals(deviceFamily, contextEngine.getDeviceFamily());
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

    private ContextEngineImpl contextEngineImpl(ContextClaimsProvider contextClaimsProvider, boolean init) {
        ContextEngineImpl contextEngine = new ContextEngineImpl();
        ReflectionTestUtils.setField(contextEngine, "deviceFamiliesFile", "device-families.xml");
        ReflectionTestUtils.setField(contextEngine, "provider", contextClaimsProvider);
        if (init) {
            ReflectionTestUtils.invokeMethod(contextEngine, "init");
        }
        return contextEngine;
    }
}
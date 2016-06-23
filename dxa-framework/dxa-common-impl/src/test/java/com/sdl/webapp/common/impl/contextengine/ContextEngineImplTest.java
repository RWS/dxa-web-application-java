package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static com.sdl.webapp.common.impl.contextengine.Claims.alienDeviceClaims;
import static com.sdl.webapp.common.impl.contextengine.Claims.appleClaims;
import static com.sdl.webapp.common.impl.contextengine.Claims.desktopClaims;
import static com.sdl.webapp.common.impl.contextengine.Claims.featurePhoneClaims;
import static com.sdl.webapp.common.impl.contextengine.Claims.smartPhoneClaims;
import static com.sdl.webapp.common.impl.contextengine.Claims.tabletClaims;
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

    @Test
    public void shouldPreferDeviceFamilyFromClaimsProvider() throws DxaException {
        //given
        ContextClaimsProvider claimsProvider = mock(ContextClaimsProvider.class);
        ContextEngineImpl contextEngine = contextEngineImpl(claimsProvider, false);
        String expected = "FromProvider";

        //when
        when(claimsProvider.getDeviceFamily()).thenReturn(expected);

        //then
        assertEquals(expected, contextEngine.getDeviceFamily());
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

    private ContextEngineImpl contextEngineImpl(ContextClaimsProvider contextClaimsProvider, boolean init) {
        DeviceFamiliesEvaluator evaluator = new DeviceFamiliesEvaluator();
        ReflectionTestUtils.setField(evaluator, "deviceFamiliesFile", "device-families.xml");
        if (init) {
            ReflectionTestUtils.invokeMethod(evaluator, "init");
        }

        ContextEngineImpl contextEngine = new ContextEngineImpl();
        ReflectionTestUtils.setField(contextEngine, "provider", contextClaimsProvider);
        ReflectionTestUtils.setField(contextEngine, "deviceFamiliesEvaluator", evaluator);
        return contextEngine;
    }
}
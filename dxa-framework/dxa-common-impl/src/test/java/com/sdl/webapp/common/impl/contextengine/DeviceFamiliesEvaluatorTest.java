package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.exceptions.DxaException;
import org.jetbrains.annotations.Nullable;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class DeviceFamiliesEvaluatorTest {

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
    public void shouldReturnNullIfNoDeviceFamiliesRules() {
        //given
        DeviceFamiliesEvaluator evaluator = new DeviceFamiliesEvaluator();

        //when
        @Nullable String deviceFamily = evaluator.defineDeviceFamily(desktopClaims());

        //then
        assertNull(deviceFamily);
    }

    @Test
    public void shouldReadFileOnlyOnce() {
        //given
        DeviceFamiliesEvaluator evaluator = deviceFamiliesEvaluator(true);
        Object rules = ReflectionTestUtils.getField(evaluator, "deviceFamiliesRules");

        //when
        ReflectionTestUtils.invokeMethod(evaluator, "init");

        //then
        assertSame(rules, ReflectionTestUtils.getField(evaluator, "deviceFamiliesRules"));
    }

    @Test
    public void shouldFallbackIfResourceXmlIsNotFound() {
        //given
        DeviceFamiliesEvaluator evaluator = new DeviceFamiliesEvaluator();
        ReflectionTestUtils.setField(evaluator, "deviceFamiliesFile", "123.xml");

        //when
        ReflectionTestUtils.invokeMethod(evaluator, "init");

        //then
        assertNull(ReflectionTestUtils.getField(evaluator, "deviceFamiliesRules"));
    }

    @Test
    public void shouldDefineAlienDeviceThatProvesWeAreNotGettingFallback() throws DxaException {
        shouldResolveDeviceFamily("aliendevice", alienDeviceClaims());
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
    public void shouldFallbackToTabletDefault() throws DxaException {
        shouldFallbackDeviceFamily("tablet", tabletClaims());
    }

    @Test
    public void shouldFallbackToDesktopDefault() throws DxaException {
        shouldFallbackDeviceFamily("desktop", desktopClaims());
    }

    @Test
    public void shouldFallbackToDesktopIfNoMatchDefault() throws DxaException {
        shouldFallbackDeviceFamily("desktop", alienDeviceClaims());
    }

    private void shouldFallbackDeviceFamily(String deviceFamily, Map<String, Object> claims) throws DxaException {
        //given
        DeviceFamiliesEvaluator deviceFamiliesEvaluator = deviceFamiliesEvaluator(false);
        DeviceClaims deviceClaims = new DeviceClaims();
        deviceClaims.setClaims(claims);

        //then
        assertEquals(deviceFamily, deviceFamiliesEvaluator.fallbackDeviceFamily(deviceClaims));
    }

    private void shouldResolveDeviceFamily(String deviceFamily, Map<String, Object> claims) throws DxaException {
        //given
        DeviceFamiliesEvaluator deviceFamiliesEvaluator = deviceFamiliesEvaluator(true);

        //then
        assertEquals(deviceFamily, deviceFamiliesEvaluator.defineDeviceFamily(claims));
    }

    private DeviceFamiliesEvaluator deviceFamiliesEvaluator(boolean init) {
        DeviceFamiliesEvaluator deviceFamiliesEvaluator = new DeviceFamiliesEvaluator();
        ReflectionTestUtils.setField(deviceFamiliesEvaluator, "deviceFamiliesFile", "device-families.xml");
        if (init) {
            ReflectionTestUtils.invokeMethod(deviceFamiliesEvaluator, "init");
        }
        return deviceFamiliesEvaluator;
    }
}
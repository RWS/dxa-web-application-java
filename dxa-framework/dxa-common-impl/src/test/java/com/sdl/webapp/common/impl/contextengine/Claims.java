package com.sdl.webapp.common.impl.contextengine;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

abstract class Claims {

    static Map<String, Object> desktopClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.mobile", false)
                .put("device.tablet", false)
                .build();
    }

    static Map<String, Object> tabletClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.tablet", true)
                .build();
    }

    static Map<String, Object> smartPhoneClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.mobile", true)
                .put("device.tablet", false)
                .put("device.displayWidth", 330)
                .build();
    }

    static Map<String, Object> alienDeviceClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.mobile", false)
                .put("device.tablet", false)
                .put("device.displayWidth", 300)
                .build();
    }

    static Map<String, Object> appleClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("os.vendor", "Apple")
                .build();
    }

    static ImmutableMap<String, Object> featurePhoneClaims() {
        return ImmutableMap.<String, Object>builder()
                .put("device.mobile", true)
                .put("device.tablet", false)
                .put("device.displayWidth", 300)
                .build();
    }

    static Map<String, Object> getClaims(String key, Object value) {
        return ImmutableMap.of(key,value);
    }
}

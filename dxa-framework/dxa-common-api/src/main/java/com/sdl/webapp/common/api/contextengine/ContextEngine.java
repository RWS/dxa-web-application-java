package com.sdl.webapp.common.api.contextengine;

public interface ContextEngine {
    <T extends ContextClaims> T getClaims(Class<T> cls);

    String getDeviceFamily();
}

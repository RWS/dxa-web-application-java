package com.sdl.webapp.common.api.contextengine;

public interface ContextEngine {
    public <T extends ContextClaims> T getClaims(Class<T> cls);

    public String getDeviceFamily();
}

package com.sdl.webapp.common.api.contextengine;

public interface ContextEngine {

    /**
     * <p>getClaims.</p>
     *
     * @param cls a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     */
    <T extends ContextClaims> T getClaims(Class<T> cls);

    /**
     * <p>getDeviceFamily.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getDeviceFamily();
}

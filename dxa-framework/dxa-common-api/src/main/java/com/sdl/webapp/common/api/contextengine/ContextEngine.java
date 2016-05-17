package com.sdl.webapp.common.api.contextengine;

public interface ContextEngine {

    //todo dxa2 return not-generic value? if not, refactor in another way
    /**
     * Returns specific claims wrapper.
     * @param cls wrapper class
     * @param <T> type of these specific claims
     * @return a wrapper
     */
    <T extends ContextClaims> T getClaims(Class<T> cls);

    /**
     * <p>Returns a predefined in a device family.</p>
     *
     * @return a device family name
     */
    String getDeviceFamily();
}

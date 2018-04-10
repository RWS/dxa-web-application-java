package com.sdl.webapp.common.api.contextengine;

import org.jetbrains.annotations.Nullable;

/**
 * Context engine gives you access to a request-scoped collection of claims and other context data.
 *
 * @dxa.publicApi
 */
public interface ContextEngine {

    /**
     * Returns specific strongly-typed claims wrapper.
     *
     * @param cls claims wrapper class
     * @param <T> type of these specific claims
     * @return a strongly-typed claims wrapper
     */
    //todo dxa2 return not-generic value? if not, refactor in another way
    @Nullable
    <T extends ContextClaims> T getClaims(Class<T> cls);

    /**
     * Returns a predefined name of a device family.
     *
     * @return a device family name
     */
    String getDeviceFamily();
}

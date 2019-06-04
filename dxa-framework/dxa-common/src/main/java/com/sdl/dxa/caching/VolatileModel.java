package com.sdl.dxa.caching;

/**
 * Interface that determines if the implementor model is cacheable and may be cached.
 *
 * @dxa.publicApi
 */
public interface VolatileModel {

    /**
     * Returns whether the model is cacheable and may be cached.
     *
     * @return whether the model never may be cached
     * @dxa.publicApi
     * This method is subject to be deleted, use isPossibleToCache instead.
     */
    @Deprecated
    default boolean canBeCached() {
        return isPossibleToCache();
    }

    /**
     * Returns whether the model is cacheable and may be cached.
     *
     * @return whether the model never may be cached
     * @dxa.publicApi
     */
    default boolean isPossibleToCache() {
        return !this.getClass().isAnnotationPresent(NeverCached.class);
    }

}

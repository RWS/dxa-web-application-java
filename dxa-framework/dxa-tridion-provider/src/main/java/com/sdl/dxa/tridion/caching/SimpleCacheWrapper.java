package com.sdl.dxa.tridion.caching;

import javax.cache.Cache;

/**
 * Wrapper on {@link Cache}.
 *
 * @param <V> value type of the cache
 */
@FunctionalInterface
public interface SimpleCacheWrapper<V> {

    /**
     * Returns current cache instance.
     *
     * @return current cache for model
     */
    Cache<Object, V> getCache();

    /**
     * Reports if caching is enabled.
     *
     * @return whether caching is enabled
     */
    default boolean isCachingEnabled() {
        return getCache() != null;
    }
}
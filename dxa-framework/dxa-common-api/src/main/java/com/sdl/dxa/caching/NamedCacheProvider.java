package com.sdl.dxa.caching;


import javax.cache.Cache;
import javax.cache.CacheManager;

/**
 * Cache provider fox DXA that prefers cache name over types.
 */
public interface NamedCacheProvider {

    /**
     * Provides cache with the given name for the given key and value types.
     * Should never return {@code null}, hence should create cache if not found.
     *
     * @param cacheName name of the cache to be created
     * @param keyType   type of the key
     * @param valueType type of the value
     * @param <K>       generic type for key
     * @param <V>       generic type for value
     * @return cache instance, never {@code null} by convention
     */
    <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType);

    /**
     * Provides cache with the given name for the given key and value types.
     * Should never return {@code null}, hence should create cache if not found.
     *
     * @param cacheName name of the cache to be created
     * @return cache instance, never {@code null} by convention
     */
    Cache<Object, Object> getCache(String cacheName);

    /**
     * Checks whether caching is enabled for the given cache name.
     *
     * @param cacheName name of the cache
     * @return whether caching is enabled
     */
    boolean isCacheEnabled(String cacheName);

    /**
     * Current cache manager used by this provider.
     *
     * @return current cache manager
     */
    CacheManager getCacheManager();
}

package com.sdl.dxa.caching;


import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Collection;

/**
 * Cache provider fox DXA that prefers cache name over types.
 *
 * @dxa.publicApi
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
     * @dxa.publicApi
     */
    <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType);

    /**
     * Provides cache with the given name for the given key and value types.
     * Should never return {@code null}, hence should create cache if not found.
     *
     * @param cacheName name of the cache to be created
     * @return cache instance, never {@code null} by convention
     * @dxa.publicApi
     */
    Cache<Object, Object> getCache(String cacheName);

    /**
     * Returns a collection of all the caches requested only through this instance of the provider.
     * Hence, the caches created or used purely by, for example, CIL are not included.
     * State of the caches is not guaranteed, thus they may be closed.
     *
     * @return collection of own current caches
     * @dxa.publicApi
     */
    Collection<Cache> getOwnCaches();

    /**
     * Checks if the cache is enabled on the global level and not for the specific caches.
     *
     * @return whether caching is globally not disabled
     * @dxa.publicApi
     */
    boolean isCacheEnabled();

    /**
     * Checks whether caching is enabled for the given cache name.
     *
     * @param cacheName name of the cache
     * @return whether caching is enabled
     * @dxa.publicApi
     */
    boolean isCacheEnabled(String cacheName);

    /**
     * Current cache manager used by this provider.
     *
     * @return current cache manager
     * @dxa.publicApi
     */
    CacheManager getCacheManager();
}

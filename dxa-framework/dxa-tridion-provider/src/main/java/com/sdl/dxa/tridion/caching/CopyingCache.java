package com.sdl.dxa.tridion.caching;

import com.rits.cloning.Cloner;

import javax.cache.Cache;
import java.util.function.Supplier;

/**
 * Cache wrapper interface that always returns a deep clone of the value from cache.
 *
 * @param <K> key type of the cache
 * @param <V> value type of the cache
 */
public interface CopyingCache<K, V> extends SimpleCacheWrapper<K, V> {

    /**
     * Gets the value from cache or, if missing, get it from supplier, and puts in cache.
     * Never returns the original value from cache but only its deep copy.
     * If caching is not enabled simply returns value from supplier.
     *
     * @param key      key of the value
     * @param supplier supplier of the value if value is missing in cache
     * @return deep copy of cached value
     */
    @SuppressWarnings("unchecked")
    default V getOrAdd(K key, Supplier<V> supplier) {
        if (!isCachingEnabled()) {
            return supplier.get();
        }

        V value;
        Cache<K, V> cache = getCache();

        if (cache.containsKey(key)) {
            value = cache.get(key);
        } else {
            value = supplier.get();
            cache.put(key, value);
        }
        return new Cloner().deepClone(value);
    }
}

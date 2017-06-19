package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.cache.Cache;
import java.util.function.Supplier;

/**
 * Wrapper on {@link Cache}.
 *
 * @param <V> value type of the cache
 */
@Slf4j
public abstract class SimpleCacheWrapper<V> {

    private final LocalizationAwareKeyGenerator keyGenerator;

    SimpleCacheWrapper(LocalizationAwareKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    /**
     * Returns current cache instance.
     *
     * @return current cache for model
     */
    public abstract Cache<Object, V> getCache();

    /**
     * Reports if caching is enabled.
     *
     * @return whether caching is enabled
     */
    public boolean isCachingEnabled() {
        return getCache() != null;
    }

    /**
     * Gets the value from cache or, if missing, get it from supplier, and puts in cache.
     * Never returns the original value from cache but only its deep copy.
     * If caching is not enabled simply returns value from supplier.
     *
     * @param valueSupplier supplier of the value if value is missing in cache
     * @param keyParams     var-args arrays of params to form the key for the value
     * @return deep copy of cached value
     */
    public V getOrAdd(Supplier<V> valueSupplier, Object... keyParams) {
        if (!isCachingEnabled()) {
            return valueSupplier.get();
        }

        V value;
        Cache<Object, V> cache = getCache();
        Object key = this.keyGenerator.generate(keyParams);
        if (cache.containsKey(key)) {
            value = cache.get(key);
            log.trace("Cache entry for key '{}' found in cache '{}'", key, cache.getName());
        } else {
            value = valueSupplier.get();
            cache.put(key, value);
            log.trace("No cache entry for key '{}' in cache '{}', the value is now put in cache", key, cache.getName());
        }
        return value;
    }
}
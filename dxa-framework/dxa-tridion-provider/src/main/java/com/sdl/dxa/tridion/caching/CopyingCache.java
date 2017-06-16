package com.sdl.dxa.tridion.caching;

import com.rits.cloning.Cloner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import javax.cache.Cache;
import java.util.function.Supplier;

/**
 * Cache wrapper abstract class that always returns a deep clone of the value from cache.
 *
 * @param <V> value type of the cache
 */
@Slf4j
public abstract class CopyingCache<V> //NOSONAR
        implements SimpleCacheWrapper<V> {

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
        Object key = SimpleKeyGenerator.generateKey(keyParams);
        if (cache.containsKey(key)) {
            value = cache.get(key);
            log.trace("Cache entry for key '{}' found in cache '{}'", key, cache.getName());
        } else {
            value = valueSupplier.get();
            cache.put(key, value);
            log.trace("No cache entry for key '{}' in copying cache '{}', the value is now put in cache", key, cache.getName());
        }
        return new Cloner().deepClone(value);
    }
}

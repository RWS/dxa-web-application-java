package com.sdl.dxa.caching;

import com.rits.cloning.Cloner;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Cache wrapper abstract class that always returns a deep clone of the value from cache.
 *
 * @param <V> value type of the cache
 */
@Slf4j
public abstract class CopyingCache<V> extends SimpleCacheWrapper<V> {

    CopyingCache(LocalizationAwareKeyGenerator keyGenerator) {
        super(keyGenerator);
    }

    /**
     * {@inheritDoc}
     * <p>Returns deep copy of the instance from cache.</p>
     */
    @Override
    public V getOrAdd(Supplier<V> valueSupplier, Object... keyParams) {
        V value = super.getOrAdd(valueSupplier, keyParams);
        return isCachingEnabled() ? new Cloner().deepClone(value) : value;
    }
}

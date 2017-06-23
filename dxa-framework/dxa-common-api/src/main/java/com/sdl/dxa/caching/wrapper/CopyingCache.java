package com.sdl.dxa.caching.wrapper;

import com.rits.cloning.Cloner;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * Cache wrapper abstract class that always returns a deep clone of the value from cache.
 *
 * @param <V> value type of the cache
 */
@Slf4j
public abstract class CopyingCache<K, V> extends SimpleCacheWrapper<K, V> {

    /**
     * {@inheritDoc}
     * <p>Returns deep copy of the instance from cache.</p>
     */
    @Override
    public V addAndGet(Object key, V value) {
        return _clone(super.addAndGet(key, value));
    }

    @Nullable
    @Override
    public V get(Object key) {
        return _clone(super.get(key));
    }

    private V _clone(V value) {
        return isCachingEnabled() ? new Cloner().deepClone(value) : value;
    }
}

package com.sdl.dxa.caching.wrapper;

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
        return _checkAndCopy(super.addAndGet(key, value));
    }

    @Nullable
    @Override
    public V get(Object key) {
        return _checkAndCopy(super.get(key));
    }

    protected abstract V copy(V value);

    private V _checkAndCopy(V value) {
        return isCachingEnabled() ? copy(value) : value;
    }

}

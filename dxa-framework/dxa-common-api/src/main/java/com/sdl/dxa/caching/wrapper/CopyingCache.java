package com.sdl.dxa.caching.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated
 * Cache wrapper abstract class that always returns a deep clone of the value from cache.
 *
 * @param <B> base type used for a key calculation in {@link #getSpecificKey(Object, Object...)}
 * @param <V> value type of the cache
 */
@Slf4j
@Deprecated
public abstract class CopyingCache<B, V> extends SimpleCacheWrapper<B, V> {

    /**
     * {@inheritDoc}
     * <p>Returns deep copy of the instance from cache.</p>
     */
    @Override
    public V addAndGet(Object key, V value) {
        return checkAndCopy(super.addAndGet(key, value));
    }

    @Nullable
    @Override
    public V get(Object key) {
        return checkAndCopy(super.get(key));
    }

    protected abstract V copy(V value);

    private V checkAndCopy(V value) {
        if (value == null) {
            return null;
        }
        return isCachingEnabled() ? copy(value) : value;
    }

}

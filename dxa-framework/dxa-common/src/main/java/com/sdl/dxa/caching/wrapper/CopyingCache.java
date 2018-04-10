package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.LocalizationAwareCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * Cache wrapper abstract class that always returns a deep clone of the value from cache.
 *
 * @param <B> base type used for a key calculation in {@link #getSpecificKey(Object, Object...)}
 * @param <V> value type of the cache
 * @dxa.publicApi
 */
@Slf4j
public abstract class CopyingCache<B, V> extends SimpleCacheWrapper<B, V> {

    /**
     * {@inheritDoc}
     * <p>Returns deep copy of the instance from cache.</p>
     */
    @Override
    public V addAndGet(LocalizationAwareCacheKey key, V value) {
        return _checkAndCopy(super.addAndGet(key, value));
    }

    @Nullable
    @Override
    public V get(LocalizationAwareCacheKey key) {
        return _checkAndCopy(super.get(key));
    }

    protected abstract V copy(V value);

    private V _checkAndCopy(V value) {
        return isCachingEnabled() ? copy(value) : value;
    }

}

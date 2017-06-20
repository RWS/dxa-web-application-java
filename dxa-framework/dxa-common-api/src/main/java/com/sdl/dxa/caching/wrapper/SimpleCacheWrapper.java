package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import com.sdl.dxa.caching.NeverCached;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.cache.Cache;

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
     * Checks if conditional key doesn't prevent caching and procedd wth {@link #addAndGet(Object, Object)}.
     *
     * @param key   conditional key with a key formed by {@link #getKey(Object...)} and a flag whether this needs to be cached
     * @param value value to cache
     * @return value put in cache
     */
    public V addAndGet(@NotNull ConditionalKey key, V value) {
        if (key.isSkipCaching()) {
            log.trace("Value for key {} is not cached", key);
            return value;
        }

        return addAndGet(key.getKey(), value);
    }

    /**
     * Puts the given value into cache unless the value class is annotated with {@link NeverCached}.
     *
     * @param value value to cache
     * @param key   key formed by {@link #getKey(Object...)}
     * @return value put in cache
     */
    public V addAndGet(Object key, V value) {
        if (!isCachingEnabled()) {
            return value;
        }

        if (value.getClass().isAnnotationPresent(NeverCached.class)) {
            log.trace("Value of class {} is never cached", value.getClass());
            return value;
        }

        Cache<Object, V> cache = getCache();
        cache.put(key, value);
        _logPut(key, cache.getName());
        return value;
    }

    /**
     * Gets a value from cache if found or {@code null} otherwise.
     *
     * @param key key formed by {@link #getKey(Object...)}
     * @return value from cache of {@code null} if not found
     */
    @Nullable
    public V get(Object key) {
        return containsKey(key) ? getCache().get(key) : null;
    }

    /**
     * Constructs the key value used in this cache.
     *
     * @param keyParams set of params to form the key
     * @return the cache key
     */
    public Object getKey(Object... keyParams) {
        return this.keyGenerator.generate(keyParams);
    }

    /**
     * Returns whether caching is enabled and the key based on list of params is cached.
     *
     * @param key key formed by {@link #getKey(Object...)}
     * @return whether key is in cache
     */
    public boolean containsKey(Object key) {
        if (!isCachingEnabled()) {
            return false;
        }
        boolean contains = getCache().containsKey(key);
        if (contains) {
            _logHit(key, getCache().getName());
        } else {
            _logMiss(key, getCache().getName());
        }
        return contains;
    }

    private void _logPut(Object key, String cacheName) {
        log.trace("Cache entry for key '{}' put in cache '{}'", key, cacheName);
    }

    private void _logHit(Object key, String cacheName) {
        log.trace("Cache entry for key '{}' found in cache '{}'", key, cacheName);
    }

    private void _logMiss(Object key, String cacheName) {
        log.trace("No cache entry for key '{}' in cache '{}', the value is now put in cache", key, cacheName);
    }
}
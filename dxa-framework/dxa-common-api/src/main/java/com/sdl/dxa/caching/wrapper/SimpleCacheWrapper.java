package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.ConditionalKey;
import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import com.sdl.dxa.caching.NamedCacheProvider;
import com.sdl.dxa.caching.NeverCached;
import com.sdl.webapp.common.api.model.ViewModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import javax.cache.Cache;

/**
 * Wrapper on {@link Cache}.
 *
 * @param <B> base type used for a key calculation in {@link #getSpecificKey(Object, Object...)}
 * @param <V> value type of the cache
 */
@Slf4j
public abstract class SimpleCacheWrapper<B, V> {

    private LocalizationAwareKeyGenerator keyGenerator;

    private NamedCacheProvider cacheProvider;

    @Autowired
    public void setKeyGenerator(LocalizationAwareKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Autowired
    public void setCacheProvider(NamedCacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @PostConstruct
    public void init() {
        log.debug("Creating of cache {} on startup", getCacheName());
        getCache();
    }

    /**
     * Returns current cache name.
     *
     * @return cache name
     */
    public abstract String getCacheName();

    /**
     * Returns current cache value type.
     *
     * @return value type
     */
    public abstract Class<V> getValueType();

    /**
     * Returns current cache instance where:
     * keys are LocalizationAwareCacheKey.class and
     * values are V.class.
     *
     * @return current cache for model
     */
    public Cache<Object, Object> getCache() {
        return cacheProvider.getCache(getCacheName());
    }

    /**
     * Reports if caching is enabled.
     *
     * @return whether caching is enabled
     */
    public boolean isCachingEnabled() {
        return cacheProvider.isCacheEnabled(getCacheName());
    }

    /**
     * Checks if conditional key doesn't prevent caching and proceed with {@link #addAndGet(Object, Object)}.
     *
     * @param key   conditional key with a key formed by {@link #getSpecificKey(Object, Object...)} and a flag whether this needs to be cached
     * @param value value to cache
     * @return value put in cache
     */
    public V addAndGet(@NotNull ConditionalKey key, V value) {
        if (key.isSkipCaching() || (value instanceof ViewModel && !((ViewModel) value).canBeCached())) {
            log.trace("Value for key {} is not cached", key);
            return value;
        }

        return addAndGet(key.getKey(), value);
    }

    /**
     * Puts the given value into cache unless the value class is annotated with {@link NeverCached}.
     *
     * @param value value to cache
     * @param key   key formed by {@link #getSpecificKey(Object, Object...)}
     * @return value put in cache
     */
    public V addAndGet(Object key, V value) {
        if (!isCachingEnabled()) {
            return value;
        }

        if (value == null) {
            return null;
        }

        if (value.getClass().isAnnotationPresent(NeverCached.class)) {
            log.trace("Value of class {} is never cached", value.getClass());
            return value;
        }

        getCache().put(key, value);
        logPut(key, getCache().getName());
        return value;
    }

    /**
     * Gets a value from cache if found or {@code null} otherwise.
     *
     * @param key key formed by {@link #getSpecificKey(Object, Object...)}
     * @return value from cache of {@code null} if not found
     */
    @Nullable
    public V get(Object key) {
        return containsKey(key) ? (V)getCache().get(key) : null;
    }

    /**
     * Constructs the key value used in this cache.
     *
     * @param keyBase   base required object to construct specific key
     * @param keyParams set of params to form the key
     * @return the cache key
     */
    public abstract Object getSpecificKey(B keyBase, Object... keyParams);

    /**
     * Returns whether caching is enabled and the key based on list of params is cached.
     *
     * @param key key formed by {@link #getSpecificKey(Object, Object...)}
     * @return whether key is in cache
     */
    public boolean containsKey(Object key) {
        if (!isCachingEnabled()) {
            return false;
        }
        boolean contains = getCache().containsKey(key);
        if (contains) {
            logHit(key, getCache().getName());
        } else {
            logMiss(key, getCache().getName());
        }
        return contains;
    }

    protected Object getKey(Object... keyParams) {
        return this.keyGenerator.generate(keyParams);
    }

    private void logPut(Object key, String cacheName) {
        log.trace("Cache entry for key '{}' put in cache '{}'", key, cacheName);
    }

    private void logHit(Object key, String cacheName) {
        log.trace("Cache entry for key '{}' found in cache '{}'", key, cacheName);
    }

    private void logMiss(Object key, String cacheName) {
        log.trace("No cache entry for key '{}' in cache '{}'", key, cacheName);
    }
}
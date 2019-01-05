package com.sdl.dxa.caching;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.support.NoOpCacheManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stores a collection of Spring's {@link Cache}s.
 */
public class SpringJCacheManagerAdapter implements CacheManager {

    private final NamedCacheProvider cacheProvider;

    private final NoOpCacheManager noOpCacheManager = new NoOpCacheManager();

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    public SpringJCacheManagerAdapter(@NotNull NamedCacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Override
    public Cache getCache(String name) {
        if (!caches.containsKey(name)) {
            Cache cache = cacheProvider.isCacheEnabled(name) ?
                    new JCacheCache(cacheProvider.getCache(name)) : noOpCacheManager.getCache(name);
            caches.putIfAbsent(name, cache);
        }
        return caches.get(name);
    }

    @Override
    public synchronized Collection<String> getCacheNames() {
        return caches.keySet();
    }
}

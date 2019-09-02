package com.sdl.dxa.caching;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.support.NoOpCacheManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stores a collection of Spring's {@link Cache}s.
 */
@Slf4j
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
            boolean cacheEnabled = cacheProvider.isCacheEnabled(name);
            log.info("Cache [{}] is {}", name, cacheEnabled ? "enabled" : "disabled");
            Cache cache = cacheEnabled
                    ? new JCacheCache(cacheProvider.getCache(name))
                    : noOpCacheManager.getCache(name);
            caches.putIfAbsent(name, cache);
        }
        Cache cache = caches.get(name);
        if (log.isDebugEnabled()) log.debug("Got cache {} -> {}", name, cache.getClass().getCanonicalName());
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return new ArrayList<>(caches.keySet());
    }
}

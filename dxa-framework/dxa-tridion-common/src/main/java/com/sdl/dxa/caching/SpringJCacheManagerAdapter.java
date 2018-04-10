package com.sdl.dxa.caching;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.support.NoOpCache;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stores a collection of Spring's {@link Cache}s.
 */
@Slf4j
public class SpringJCacheManagerAdapter implements CacheManager {

    private final NamedCacheProvider cacheProvider;

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    public SpringJCacheManagerAdapter(@NotNull NamedCacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Override
    public Cache getCache(String name) {
        log.debug("Requested cache name = {}, we know these caches: {}", caches);
        if (!caches.containsKey(name)) {
            Cache cache = cacheProvider.isCacheEnabled(name) ?
                    new JCacheCache(cacheProvider.getCache(name)) : new NoOpCache(name);
            log.debug("Created a cache {} and now know all these: {}", name, caches);
            caches.putIfAbsent(name, cache);
        }
        return caches.get(name);
    }

    @Override
    public synchronized Collection<String> getCacheNames() {
        return caches.keySet();
    }
}

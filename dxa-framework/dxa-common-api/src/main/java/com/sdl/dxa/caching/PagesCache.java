package com.sdl.dxa.caching;

import com.sdl.webapp.common.api.model.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.CacheManager;

@Component
public class PagesCache extends SimpleCacheWrapper<PageModel> {

    private Cache<Object, PageModel> pages;

    private CacheManager cacheManager;

    @Autowired
    public PagesCache(LocalizationAwareKeyGenerator keyGenerator) {
        super(keyGenerator);
    }

    @Autowired(required = false) // cannot autowire in constructor because CacheManager may not exist
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        pages = cacheManager == null ? null : cacheManager.getCache("pages");
    }

    @Override
    public Cache<Object, PageModel> getCache() {
        return pages;
    }
}

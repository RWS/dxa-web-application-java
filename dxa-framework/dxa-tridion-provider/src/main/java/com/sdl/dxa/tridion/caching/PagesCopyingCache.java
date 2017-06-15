package com.sdl.dxa.tridion.caching;

import com.sdl.webapp.common.api.model.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.cache.Cache;

@Component
public class PagesCopyingCache implements CopyingCache<String, PageModel> {

    private final PagesCache pageCache;

    @Autowired
    public PagesCopyingCache(PagesCache pageCache) {
        this.pageCache = pageCache;
    }

    @Override
    public Cache<String, PageModel> getCache() {
        return pageCache.getCache();
    }
}

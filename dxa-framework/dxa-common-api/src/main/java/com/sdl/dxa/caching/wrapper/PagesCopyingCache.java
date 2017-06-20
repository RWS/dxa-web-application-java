package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import com.sdl.webapp.common.api.model.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.cache.Cache;

@Component
public class PagesCopyingCache extends CopyingCache<PageModel> {

    private final PagesCache pageCache;

    @Autowired
    public PagesCopyingCache(LocalizationAwareKeyGenerator keyGenerator, PagesCache pageCache) {
        super(keyGenerator);
        this.pageCache = pageCache;
    }

    public Object getKey(PageModelData pageModelData) {
        return pageCache.getKey(pageModelData);
    }

    @Override
    public Cache<Object, PageModel> getCache() {
        return pageCache.getCache();
    }
}

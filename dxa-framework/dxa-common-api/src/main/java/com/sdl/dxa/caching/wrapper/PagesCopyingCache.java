package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.webapp.common.api.model.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PagesCopyingCache extends CopyingCache<PageModelData, PageModel> {

    private final PagesCache pageCache;

    @Autowired
    public PagesCopyingCache(PagesCache pageCache) {
        this.pageCache = pageCache;
    }

    @Override
    public String getCacheName() {
        return pageCache.getCacheName();
    }

    @Override
    public Object getSpecificKey(PageModelData pageModelData, Object... keyParams) {
        return pageCache.getSpecificKey(pageModelData, keyParams);
    }
}

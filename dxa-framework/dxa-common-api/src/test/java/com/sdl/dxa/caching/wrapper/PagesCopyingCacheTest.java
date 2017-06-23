package com.sdl.dxa.caching.wrapper;

import com.sdl.webapp.common.api.model.PageModel;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PagesCopyingCacheTest {

    @Test
    public void shouldUseSimplePagesCache_ToGetCache() {
        //given 
        CacheManager cacheManager = mock(CacheManager.class);
        //noinspection unchecked
        Cache<Object, Object> cache = mock(Cache.class);
        when(cacheManager.getCache(eq("pages"))).thenReturn(cache);

        PagesCache pagesCache = mock(PagesCache.class);
        when(pagesCache.getCacheName()).thenCallRealMethod();
        pagesCache.setCacheManager(cacheManager);

        PagesCopyingCache pagesCopyingCache = new PagesCopyingCache(pagesCache);
        pagesCopyingCache.setCacheManager(cacheManager);

        //when
        Cache<Object, PageModel> actual = pagesCopyingCache.getCache();

        //then
        assertSame(cache, actual);
    }
}
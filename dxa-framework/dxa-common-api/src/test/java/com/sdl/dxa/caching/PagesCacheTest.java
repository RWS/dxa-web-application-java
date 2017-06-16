package com.sdl.dxa.caching;

import com.sdl.webapp.common.api.model.PageModel;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.cache.Cache;
import javax.cache.CacheManager;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class PagesCacheTest {

    @Test
    public void shouldReturnPagesCache() {
        //given 
        CacheManager cacheManager = mock(CacheManager.class);
        Cache cache = mock(Cache.class);
        doReturn(cache).when(cacheManager).getCache(eq("pages"));

        //when
        PagesCache pagesCache = new PagesCache(mock(LocalizationAwareKeyGenerator.class));
        ReflectionTestUtils.setField(pagesCache, "cacheManager", cacheManager);
        pagesCache.init();
        Cache<Object, PageModel> actual = pagesCache.getCache();

        //then
        assertSame(actual, cache);
    }
}
package com.sdl.dxa.caching.wrapper;

import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
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

        PagesCopyingCache pagesCopyingCache = new PagesCopyingCache();
        pagesCopyingCache.setCacheManager(cacheManager);

        //when
        Cache<Object, PageModel> actual = pagesCopyingCache.getCache();

        //then
        assertSame(cache, actual);
    }

    @Test
    public void shouldCreateItsCopy() {
        //given 
        DefaultPageModel pageModel = new DefaultPageModel();
        pageModel.setId("id");
        pageModel.setName("name");
        pageModel.setUrl("url");
        DefaultPageModel expectedCopy = new DefaultPageModel(pageModel);

        //when
        PageModel actualCopy = new PagesCopyingCache().copy(pageModel);

        //then
        assertNotSame(pageModel, expectedCopy);
        assertNotSame(actualCopy, expectedCopy);
        assertNotSame(actualCopy, pageModel);
        assertEquals(expectedCopy, pageModel);
        assertEquals(actualCopy, pageModel);
    }
}
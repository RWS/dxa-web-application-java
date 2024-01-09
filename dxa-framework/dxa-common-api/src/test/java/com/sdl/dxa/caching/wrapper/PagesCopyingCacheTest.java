package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.NamedCacheProvider;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import org.junit.jupiter.api.Test;

import javax.cache.Cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PagesCopyingCacheTest {

    @Test
    public void shouldUseSimplePagesCache_ToGetCache() {
        //given 
        //noinspection unchecked
        Cache<Object, Object> cache = mock(Cache.class);

        NamedCacheProvider cacheProvider = mock(NamedCacheProvider.class);
        lenient().when(cacheProvider.getCache(eq("pages"))).thenReturn(cache);

        PagesCopyingCache pagesCopyingCache = new PagesCopyingCache();
        pagesCopyingCache.setCacheProvider(cacheProvider);

        // when
        Cache<Object, Object> actual = pagesCopyingCache.getCache();

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
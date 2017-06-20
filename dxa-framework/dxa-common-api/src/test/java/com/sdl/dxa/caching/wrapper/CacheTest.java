package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CacheTest {

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private Localization localization;

    private LocalizationAwareKeyGenerator keyGenerator;

    @Before
    public void init() {
        when(localization.getId()).thenReturn("42");
        when(webRequestContext.getLocalization()).thenReturn(localization);
        keyGenerator = new LocalizationAwareKeyGenerator(webRequestContext);
    }

    @Test
    public void shouldReturnCache() {
        shouldReturnNeededCache(() -> new PagesCache(keyGenerator), "pages");

        shouldReturnNeededCache(() -> new EntitiesCache(keyGenerator), "entities");
    }

    @Test
    public void shouldDelegateKeyCalculationToConcreteCaches() {
        //given
        PagesCache pagesCache = new PagesCache(keyGenerator);
        PagesCopyingCache pagesCopyingCache = new PagesCopyingCache(keyGenerator, pagesCache);
        EntitiesCache entitiesCache = new EntitiesCache(keyGenerator);
        MvcModelData mvcData = new MvcModelData("a", "a", "a", "c", "v", null);
        PageModelData pageData = (PageModelData) new PageModelData("1", null, null, null, "/url")
                .setMvcData(mvcData);
        EntityModelData entityMvcData = (EntityModelData) new EntityModelData("2", null, null, null, null, mvcData, "1", "/url", null, null, null)
                .setMvcData(mvcData);

        //when
        Object pagesCacheKey = pagesCache.getKey(pageData);
        Object pagesCopyingCacheKey = pagesCopyingCache.getKey(pageData);
        Object entitiesCacheKey = entitiesCache.getKey(entityMvcData);

        //then
        assertEquals(pagesCopyingCacheKey, pagesCacheKey);
        assertEquals(keyGenerator.generate("/url", mvcData), pagesCacheKey);
        assertEquals(keyGenerator.generate("1", "2", mvcData), entitiesCacheKey);
    }

    private void shouldReturnNeededCache(Supplier<SimpleCacheWrapper<?>> supplier, String cacheName) {
        CacheManager cacheManager = mock(CacheManager.class);
        Cache cache = mock(Cache.class);
        doReturn(cache).when(cacheManager).getCache(eq(cacheName));

        SimpleCacheWrapper<?> objectCache = supplier.get();
        ReflectionTestUtils.setField(objectCache, "cacheManager", cacheManager);
        ReflectionTestUtils.invokeMethod(objectCache, "init");

        Cache<Object, ?> actual = objectCache.getCache();
        assertSame(cache, actual);
    }
}
package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.function.Supplier;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CacheTest {

    @Test
    public void shouldReturnCache() {
        shouldReturnNeededCache(() -> new PagesCache(mock(LocalizationAwareKeyGenerator.class)), "pages");

        shouldReturnNeededCache(() -> new EntitiesCache(mock(LocalizationAwareKeyGenerator.class)), "entities");
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
package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import com.sdl.dxa.caching.NeverCached;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.interceptor.SimpleKey;

import javax.cache.Cache;

import static javax.cache.Caching.getCachingProvider;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.jsr107.Eh107Configuration.fromEhcacheCacheConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CopyingCacheTest {

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private Localization localization;

    private TestingCache testingCache;

    @Before
    public void init() {
        when(localization.getId()).thenReturn("42");
        when(webRequestContext.getLocalization()).thenReturn(localization);

        testingCache = new TestingCache(new LocalizationAwareKeyGenerator(webRequestContext));
    }

    @Test
    public void shouldReturnItemFromCache_IfExists_CopyItem() {
        //given
        TestingValue initial = new TestingValue(1);

        //when
        TestingValue added = (TestingValue) testingCache.addAndGet("key", initial);
        TestingValue fromCache = (TestingValue) testingCache.get("key");

        //then
        assertEquals(initial, added);
        assertEquals(initial, fromCache);
        assertEquals(added, fromCache);
        assertNotSame(initial, added);
        assertNotSame(initial, fromCache);
        assertNotSame(added, fromCache);
    }

    @Test
    public void shouldReturnValue_IfCachingDisabled() {
        //given
        CopyingCache<TestingValue> cache = new CopyingCache<TestingValue>(mock(LocalizationAwareKeyGenerator.class)) {
            @Override
            public Cache<Object, TestingValue> getCache() {
                return null;
            }
        };
        TestingValue value = new TestingValue(1);

        //when
        TestingValue first = cache.addAndGet("key", value);

        //then
        assertFalse(cache.containsKey("key"));
    }

    @Test
    public void shouldNotCache_NeverCachedEntities() {
        //given

        //when
        testingCache.addAndGet("key", new TestingValue(1));
        testingCache.addAndGet("key2", new NeverCachedValue(1));

        //then
        assertTrue(testingCache.containsKey("key"));
        assertFalse(testingCache.containsKey("key2"));
    }

    @Test
    public void shouldCalculateKey() {
        //given 

        //when
        Object key = testingCache.getKey("1", "2");

        //then
        assertTrue(key instanceof SimpleKey);
        assertEquals(new SimpleKey("42", "1", "2"), key);
    }

    private static class TestingCache extends CopyingCache<Object> {

        private final static Cache<Object, Object> cache = getCachingProvider().getCacheManager()
                .createCache("test", fromEhcacheCacheConfiguration(
                        newCacheConfigurationBuilder(Object.class, Object.class,
                                ResourcePoolsBuilder.heap(10)).build()));

        TestingCache(LocalizationAwareKeyGenerator keyGenerator) {
            super(keyGenerator);
        }

        @Override
        public Cache<Object, Object> getCache() {
            return cache;
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class TestingValue {

        int field;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @NeverCached(qualifier = "name")
    private static class NeverCachedValue {

        int field;
    }
}
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
import org.springframework.test.util.ReflectionTestUtils;

import javax.cache.Cache;

import static javax.cache.Caching.getCachingProvider;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.jsr107.Eh107Configuration.fromEhcacheCacheConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
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

        LocalizationAwareKeyGenerator keyGenerator = new LocalizationAwareKeyGenerator();
        ReflectionTestUtils.setField(keyGenerator, "webRequestContext", webRequestContext);
        testingCache = new TestingCache();

        testingCache.setKeyGenerator(keyGenerator);
        testingCache.setCacheManager(getCachingProvider().getCacheManager());
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
        CopyingCache<Object, Object> cache = new CopyingCache<Object, Object>() {
            @Override
            protected Object copy(Object value) {
                return new TestingValue(((TestingValue) value).field);
            }

            @Override
            public String getCacheName() {
                return "test";
            }

            @Override
            public Cache<Object, Object> getCache() {
                return null;
            }

            @Override
            public Object getSpecificKey(Object keyBase, Object... keyParams) {
                return null;
            }
        };
        TestingValue value = new TestingValue(1);

        //when
        Object first = cache.addAndGet("key", value);

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
        Object key = testingCache.getKey("1", "2", "3");

        //then
        assertTrue(key instanceof SimpleKey);
        assertEquals(new SimpleKey("42", "1", "2", "3"), key);
    }

    private static class TestingCache extends CopyingCache<Object, Object> {

        private final static Cache<Object, Object> cache = getCachingProvider().getCacheManager()
                .createCache("test", fromEhcacheCacheConfiguration(
                        newCacheConfigurationBuilder(Object.class, Object.class,
                                ResourcePoolsBuilder.heap(10)).build()));

        @Override
        public String getCacheName() {
            return "test";
        }

        @Override
        public Cache<Object, Object> getCache() {
            return cache;
        }

        @Override
        public Object getSpecificKey(Object keyBase, Object... keyParams) {
            return super.getKey(keyBase);
        }

        @Override
        protected Object copy(Object value) {
            return new TestingValue(((TestingValue) value).field);
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class TestingValue {

        int field;
    }

    @EqualsAndHashCode(callSuper = true)
    @NeverCached(qualifier = "name")
    private static class NeverCachedValue extends TestingValue {

        int field;

        public NeverCachedValue(int field) {
            super(field);
        }
    }
}
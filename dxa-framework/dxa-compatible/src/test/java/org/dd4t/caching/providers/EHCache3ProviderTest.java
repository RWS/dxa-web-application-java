package org.dd4t.caching.providers;

import org.dd4t.caching.CacheElement;
import org.dd4t.caching.impl.CacheElementImpl;
import org.dd4t.providers.PayloadCacheProvider;
import org.ehcache.Cache;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * EHCache3ProviderTest.
 */

public class EHCache3ProviderTest {

    private static EHCache3Provider cacheProvider = getCacheProvider();

    @BeforeClass
    public static void setUp() {

    }

    private static void testCacheCount(final Cache cache, int count) {
        int i = 0;
        for (final Object o : cache) {
            i++;
        }
        assertEquals(count, i);
    }

    private static EHCache3Provider getCacheProvider() {
        PayloadCacheProvider cacheProvider = new EHCache3Provider();
        assertNotNull(cacheProvider);
        return (EHCache3Provider) cacheProvider;
    }

    @Test
    public void testCacheConstruction() {
        getCacheProvider();
    }

    @Test
    public void testStoreElement() {
        CacheElement<String> cacheElement = new CacheElementImpl<>("TEST", false);

        cacheProvider.storeInItemCache("testKey", cacheElement);
        CacheElement<String> fromCache = cacheProvider.loadPayloadFromLocalCache("testKey");
        assertNotNull(fromCache);
        assertEquals(fromCache.getPayload(), "TEST");
        assertFalse(fromCache.isExpired());
        assertFalse(fromCache.isNull());
    }

    @Test
    public void testStoreElementWithDependencyOnIds() {


        CacheElement<String> cacheElement = new CacheElementImpl<>("TEST", false);

        int dependingPubId = 300;
        int dependingItemId = 1234;

        cacheProvider.storeInItemCache("testKey", cacheElement, dependingPubId, dependingItemId);
        CacheElement<String> fromCache = cacheProvider.loadPayloadFromLocalCache("testKey");
        assertNotNull(fromCache);
        assertEquals(fromCache.getPayload(), "TEST");
        assertFalse(fromCache.isExpired());
        assertFalse(fromCache.isNull());

        testCacheCount(cacheProvider.getDependencyCache(), 1);
        testCacheCount(cacheProvider.getCache(), 1);

        Cache.Entry<String, CacheElement> depElement = (Cache.Entry<String, CacheElement>) cacheProvider.getDependencyCache().iterator().next();
        assertNotNull(depElement);

        assertEquals(depElement.getKey(), "300:1234");

        ConcurrentSkipListSet<String> payload = (ConcurrentSkipListSet<String>) depElement.getValue().getPayload();
        assertEquals(payload.first(), "testKey");

        cacheProvider.invalidate("300:1234");
        testCacheCount(cacheProvider.getDependencyCache(), 0);
        testCacheCount(cacheProvider.getCache(), 0);
    }
}
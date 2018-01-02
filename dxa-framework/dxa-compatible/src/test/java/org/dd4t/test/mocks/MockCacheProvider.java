package org.dd4t.test.mocks;

import org.dd4t.caching.CacheDependency;
import org.dd4t.caching.CacheElement;
import org.dd4t.providers.PayloadCacheProvider;

import java.util.List;

/**
 * MockCacheProvider.
 */
public class MockCacheProvider implements PayloadCacheProvider {

    @Override
    public <T> CacheElement<T> loadPayloadFromLocalCache(final String key) {
        return new CacheElement<T>() {
            @Override
            public T getPayload() {
                return null;
            }

            @Override
            public void setPayload(final T payload) {

            }

            @Override
            public boolean isExpired() {
                return true;
            }

            @Override
            public void setExpired(final boolean update) {

            }

            @Override
            public boolean isNull() {
                return true;
            }

            @Override
            public void setNull(final boolean isnull) {

            }

            @Override
            public String getDependentKey() {
                return null;
            }

            @Override
            public void setDependentKey(final String dependentKey) {

            }
        };
    }

    @Override
    public <T> void storeInItemCache(final String key, final CacheElement<T> cacheElement) {

    }

    @Override
    public <T> void storeInItemCache(final String key, final CacheElement<T> cacheElement, final int
            dependingPublicationId, final int dependingItemId) {

    }

    @Override
    public <T> void storeInItemCache(final String key, final CacheElement<T> cacheElement, final List<CacheDependency
            > dependencies) {

    }

    @Override
    public void addDependency(final String cacheKey, final String dependencyKey) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
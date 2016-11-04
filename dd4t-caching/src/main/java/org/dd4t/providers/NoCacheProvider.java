package org.dd4t.providers;

import org.dd4t.core.caching.Cachable;
import org.dd4t.core.caching.CacheDependency;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheInvalidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * dd4t-parent
 * TODO: merge with org.dd4t.providers.NoCacheProvider
 * @author R. Kempees
 */
public class NoCacheProvider implements PayloadCacheProvider, CacheInvalidator, CacheProvider {

    private static final Logger LOG = LoggerFactory.getLogger(NoCacheProvider.class);

    public NoCacheProvider () {
        LOG.info("NoCacheProvider loaded. This means the DD4T Object Cache will not cache anything.");
    }

    @Override
    public void flush () {
        LOG.debug("Not flushing as this is nothing is cached.");
    }

    @Override
    public void invalidate (final String key) {
        LOG.debug("Nothing to invalidate by design.");
    }

    @Override
    public Object loadFromLocalCache (final String key) {
        LOG.debug("Nothing to load by design.");
        return null;
    }

    @Override
    public void storeInCache (final String key, final Cachable ob, final Collection<Cachable> deps) {
        LOG.debug("Nothing to store by design.");
    }

    @Override
    public void storeInItemCache (final String key, final Object ob, final int dependingPublicationId, final int dependingItemId) {
        LOG.debug("Nothing to store by design.");
    }

    @Override
    public void storeInComponentPresentationCache (final String key, final Object ob, final int dependingPublicationId, final int dependingCompId, final int dependingTemplateId) {
        LOG.debug("Nothing to store by design.");
    }

    @Override
    public void storeInKeywordCache (final String key, final Object ob, final int dependingPublicationId, final int dependingItemId) {
        LOG.debug("Nothing to store by design.");
    }

    @Override
    public void storeInItemCache(final String key, final Object ob, final List<CacheDependency> dependencies) {
        LOG.debug("Nothing to store by design.");
    }

    @Override
    public <T> CacheElement<T> loadPayloadFromLocalCache (final String key) {
        LOG.debug("Nothing to load by design.");
        return null;
    }

    @Override
    public <T> void storeInItemCache (final String key, final CacheElement<T> cacheElement) {
        LOG.debug("Nothing to store by design.");
    }

    @Override
    public <T> void storeInItemCache (final String key, final CacheElement<T> cacheElement, final int dependingPublicationId, final int dependingItemId) {
        LOG.debug("Nothing to store by design.");
    }

    @Override
    public <T> void storeInItemCache (final String key, final CacheElement<T> cacheElement, final List<CacheDependency> dependencies) {
        LOG.debug("Nothing to store by design.");
    }

    @Override
    public void addDependency (final String cacheKey, final String dependencyKey) {
        LOG.debug("Nothing to add by design.");
    }
}

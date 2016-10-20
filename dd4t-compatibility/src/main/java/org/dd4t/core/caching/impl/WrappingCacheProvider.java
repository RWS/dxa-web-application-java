package org.dd4t.core.caching.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.dd4t.core.caching.Cachable;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.providers.CacheProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappingCacheProvider implements CacheProvider {
    private static final Logger LOG = LoggerFactory.getLogger(WrappingCacheProvider.class);
	
	@Resource
	private PayloadCacheProvider internalProvider;


    @Override
    public Object loadFromLocalCache (String key) {
        CacheElement<Object> item = internalProvider.loadPayloadFromLocalCache(key);

        if (item.isExpired()) {
            LOG.debug("Not returning expired item");
            return null;
        } else {
            return item.getPayload();
        }
    }

    @Override
    public void storeInCache (String key, Cachable ob, Collection<Cachable> deps) {

        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement);

        for (Cachable item : deps) {
        	internalProvider.addDependency(key, item.getCacheKey());
        }
    }

    @Override
    public void storeInItemCache (String key, Object ob, int dependingPublicationId, int dependingItemId) {
        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement, dependingPublicationId, dependingItemId);
    }

    @Override
    public void storeInComponentPresentationCache (String key, Object ob, int dependingPublicationId, int dependingCompId, int dependingTemplateId) {
        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement, dependingPublicationId, dependingCompId);
    }

    @Override
    public void storeInKeywordCache (String key, Object ob, int dependingPublicationId, int dependingItemId) {
        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement, dependingPublicationId, dependingItemId);
    }   
}

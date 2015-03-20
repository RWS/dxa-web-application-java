package org.dd4t.providers.impl;

import java.util.Collection;

import org.dd4t.core.caching.Cachable;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.impl.CacheElementImpl;
import org.dd4t.providers.CacheProvider;
import org.dd4t.providers.PayloadCacheProvider;

/**
 * CacheProvider which doesn't cache anything.
 * 
 * @author rooudsho
 *
 */
public class NoCacheProvider implements PayloadCacheProvider, CacheProvider {

	@Override
	public <T> void storeInItemCache(String key, CacheElement<T> cacheElement) {
		
	}

	@Override
	public <T> void storeInItemCache(String key, CacheElement<T> cacheElement,
			int dependingPublicationId, int dependingItemId) {
		
	}

	@Override
	public <T> CacheElement<T> loadPayloadFromLocalCache(String key) {
		return new CacheElementImpl<T>(null, true);
	}

	@Override
	public void storeInCache(String key, Cachable ob, Collection<Cachable> deps) {		
	}

	@Override
	public void storeInItemCache(String key, Object ob,
			int dependingPublicationId, int dependingItemId) {		
	}

	@Override
	public void storeInComponentPresentationCache(String key, Object ob,
			int dependingPublicationId, int dependingCompId,
			int dependingTemplateId) {		
	}

	@Override
	public void storeInKeywordCache(String key, Object ob,
			int dependingPublicationId, int dependingItemId) {		
	}

	@Override
	public Object loadFromLocalCache(String key) {
		return null;
	}
}

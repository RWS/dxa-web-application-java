package org.dd4t.providers.impl;

import java.util.Collection;

import org.dd4t.core.caching.Cachable;
import org.dd4t.providers.CacheProvider;

/**
 * CacheProvider which doesn't cache anything.
 * 
 * @author rooudsho
 *
 */
public class NoCacheProvider implements CacheProvider {

	@Override
	public Object loadFromLocalCache(String key) {
		return null;
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

}

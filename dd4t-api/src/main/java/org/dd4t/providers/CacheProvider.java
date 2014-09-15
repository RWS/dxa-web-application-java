package org.dd4t.providers;

import java.util.Collection;

import org.dd4t.core.caching.Cachable;

public interface CacheProvider {
	/**
	 * Loads given object from the cache
	 * 
	 * @param key
	 * @return
	 */
	public Object loadFromLocalCache(String key);
	
	/**
	 * Store given item in the cache with a reference to given collection of also cached items
	 */
	public void storeInCache(String key, Cachable ob, Collection<Cachable> deps);
	
	/**
	 * Store given item in the cache with a reference to supplied Tridion Item.
	 */
	public void storeInItemCache(String key, Object ob, int dependingPublicationId, int dependingItemId);
	
	/**
	 * Store given item in the cache with a reference to supplied Tridion Component Presentation.
	 */
	public void storeInComponentPresentationCache(String key, Object ob, int dependingPublicationId, int dependingCompId, int dependingTemplateId);
	
	/**
	 * Store given item in the cache with a reference to supplied Tridion Keyword.
	 */
	public void storeInKeywordCache(String key, Object ob, int dependingPublicationId, int dependingItemId);	
}

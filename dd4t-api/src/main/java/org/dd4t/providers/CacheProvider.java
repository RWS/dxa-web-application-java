package org.dd4t.providers;

import org.dd4t.core.caching.CacheElement;

public interface CacheProvider {

    /**
     * Loads object corresponding to the given key from cache. Method always returns a non-null CacheElement that
     * wraps around the actual payload object. Payload can be null and can also be expired, which denotes the payload
     * is stale and needs updating, but can still be used while updating is complete.
     *
     * @param key String representing the name under which the object is stored in the cache
     * @return CacheElement the wrapper around the actual payload object in cache
     */
    public <T> CacheElement<T> loadFromLocalCache(String key);

    /**
     * Store given item in the cache with a simple time-to-live property (for items not depending on Tridion items)
     *
     * @param key          String representing the name under which the object is stored in the cache
     * @param cacheElement CacheElement representing wrapper around the actual payload to store in cache
     */
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement);

    /**
     * Store given item in the cache with a reference to supplied Tridion Item.
     *
     * @param key                    String representing the name under which the object is stored in the cache
     * @param cacheElement           CacheElement representing wrapper around the actual payload to store in cache
     * @param dependingPublicationId int representing the Publication id of the Tridion item the cacheItem depends on
     * @param dependingItemId        int representing the Item id of the Tridion item the cacheItem depends on
     */
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement, int dependingPublicationId, int dependingItemId);
}

/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.caching.providers;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.dd4t.caching.CacheDependency;
import org.dd4t.caching.CacheElement;
import org.dd4t.caching.CacheInvalidator;
import org.dd4t.caching.impl.CacheElementImpl;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * EH Cache implementation
 * <p>
 * TODO: this class has bugs! Only use for Tridion Object invalidation!
 *
 * @author R. Kempees, Mihai Cadariu, Rogier Oudshoorn
 */
public class EHCacheProvider extends AbstractEHCacheProvider implements PayloadCacheProvider, CacheInvalidator {

    private final Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);
    private final Cache dependencyCache = CacheManager.create().getCache(CACHE_NAME_DEPENDENCY);

    private static final Logger LOG = LoggerFactory.getLogger(EHCacheProvider.class);

    private int expiredTTL = 299;
    private int cacheDependencyTTL = 299;
    private int cacheTTL = 3599;

    /*
     * Getters and setters for cache TTL's
     */
    public int getCacheDependencyTTL() {
        return cacheDependencyTTL;
    }

    public void setCacheDependencyTTL(int cacheDependencyTTL) {
        this.cacheDependencyTTL = cacheDependencyTTL;
    }

    public int getCacheTTL() {
        return cacheTTL;
    }

    public void setCacheTTL(int cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    public Cache getDependencyCache() {
        return dependencyCache;
    }

    public int getExpiredTTL() {
        return expiredTTL;
    }

    public void setExpiredTTL(int expiredTTL) {
        this.expiredTTL = expiredTTL;
    }

    /**
     * Loads given object from the cache and returns it inside a CacheItem
     * wrapper.
     *
     * @param key String representing the cache key to retrieve a payload for
     * @return CacheElement object wrapping the actual payload. Return object
     * never null, but it can wrap a null payload. It can also return an
     * expired (stale) payload, which must be updated.
     */
    @Override
    public <T> CacheElement<T> loadPayloadFromLocalCache(String key) {

        if (!isEnabled()) {
            LOG.debug("Cache is disabled. Returning a null Cache Element.");
            return new CacheElementImpl<>(null, true);
        }

        if (!doCheckForPreview() || (TridionUtils.getSessionPreviewToken() == null && cache != null)) {
            Element currentElement = cache.get(key);
            if (currentElement == null) {
                currentElement = new Element(key, new CacheElementImpl<T>(null));
                setExpired(currentElement, 0);
                Element oldElement = cache.putIfAbsent(currentElement);
                if (oldElement != null) {
                    currentElement = oldElement;
                }
            }

            CacheElement<T> cacheElement = (CacheElement<T>) currentElement.getObjectValue();

            String dependencyKey = cacheElement.getDependentKey();
            if (dependencyKey != null) {
                Element dependencyElement = dependencyCache.get(dependencyKey); // update
                // LRU
                // stats
                if (dependencyElement == null) { // recover evicted dependent
                    // key
                    addDependency(key, dependencyKey);
                }
            }
            return cacheElement;
        } else {
            LOG.debug("Disable cache for Preview Session Token: {}", TridionUtils.getSessionPreviewToken());
            return new CacheElementImpl<T>(null, true);
        }
    }

    @Override
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement, List<CacheDependency> dependencies) {
        if (!isEnabled()) {
            return;
        }

        if (!cacheExists()) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }

        // detect undeclared nulls, complain, and set to null
        if (!cacheElement.isNull() && cacheElement.getPayload() == null) {
            Exception exToLogToHaveStacktraceWhoCausedIt = new Exception();
            LOG.error("Detected undeclared null payload on element with key " + key + " at insert time!",
                    exToLogToHaveStacktraceWhoCausedIt);
            cacheElement.setNull(true);
        }

        cacheElement.setExpired(false);
        Element element = cache.get(key);
        if (element == null) {
            element = new Element(key, cacheElement);
        }

        cache.put(element);

        element.setTimeToLive(cacheDependencyTTL);

        for (CacheDependency dep : dependencies) {
            String dependentKey = getKey(dep.getPublicationId(), dep.getItemId());
            cacheElement.setDependentKey(dependentKey);
            addDependency(key, dependentKey);
            updateTTL(dependencyCache.get(dependentKey));
        }
    }

    /*
     * Makes the _fromKey_ dependent on _toKey_ It adds the _fromKey_ to the
     * list of values that depend on the _toKey_
     */
    @Override
    public void addDependency(String cacheKey, String dependencyKey) {

        if (!isEnabled()) {
            return;
        }

        if (dependencyCache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        ConcurrentSkipListSet<String> cacheSet = null;
        Element dependencyElement = dependencyCache.get(dependencyKey);
        if (dependencyElement != null) {
            cacheSet = ((CacheElement<ConcurrentSkipListSet<String>>) dependencyElement.getObjectValue()).getPayload();
            setNotExpired(dependencyElement);
        }
        if (cacheSet == null) {
            LOG.debug("Add empty dependencies for key: {} to cache", dependencyKey);
            cacheSet = new ConcurrentSkipListSet<>();
            CacheElementImpl<ConcurrentSkipListSet<String>> cacheElement = new
                    CacheElementImpl<ConcurrentSkipListSet<String>>(cacheSet);
            Element newElement = new Element(dependencyKey, cacheElement);
            newElement.setTimeToLive(cacheDependencyTTL);
            Element oldElement = dependencyCache.putIfAbsent(newElement);
            if (oldElement != null) {
                ConcurrentSkipListSet<String> oldCacheSet = ((CacheElement<ConcurrentSkipListSet<String>>) oldElement
                        .getObjectValue()).getPayload();
                if (oldCacheSet != null) {
                    cacheSet.addAll(oldCacheSet);
                }
            }
        }
        LOG.debug("Add dependency from key: {} to key: {}", dependencyKey, cacheKey);
        cacheSet.add(cacheKey);
    }

    @Override
    protected boolean cacheExists() {
        return cache != null;
    }

    @Override
    protected boolean dependencyCacheExists() {
        return dependencyCache != null;
    }

    @Override
    protected <T> void storeElement(final String key, CacheElement<T> cacheElement) {
        if (!isEnabled()) {
            return;
        }

        final Element element = new Element(key, cacheElement);
        element.setTimeToLive(cacheTTL);

        if (cache.isKeyInCache(key)) {
            cache.replace(element);
        } else {
            cache.put(element);
        }
    }

    @Override
    public void flush() {
        if (!isEnabled()) {
            return;
        }

        if (cache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        LOG.info("Expire all items in cache");
        for (Object key : cache.getKeys()) {
            setExpired(cache.get(key), 0);
        }
        for (Object key : dependencyCache.getKeys()) {
            setExpired(dependencyCache.get(key), ADJUST_TTL);
        }
    }

    @Override
    public void invalidate(final String key) {
        if (!isEnabled()) {
            return;
        }

        if (dependencyCache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        String dependencyKey = getKey(key);
        Element dependencyElement = dependencyCache.get(dependencyKey);
        if (dependencyElement != null) {
            LOG.info("Expire key: {} from dependency cache", dependencyKey);
            setExpired(dependencyElement, ADJUST_TTL);
            ConcurrentSkipListSet<String> cacheSet = ((CacheElement<ConcurrentSkipListSet<String>>) dependencyElement
                    .getObjectValue()).getPayload();
            if (cacheSet != null) {
                for (String cacheKey : cacheSet) {
                    LOG.debug("Expire cache key: {} from cache", cacheKey);
                    Element cacheElement = cache.get(cacheKey);
                    setExpired(cacheElement, 0);
                }
            }
        } else {
            LOG.info("Attempting to expire key {} but not found in dependency cache", dependencyKey);
        }
    }

    public void setExpired(Element element, int adjustTTL) {

        if (!isEnabled()) {
            return;
        }

        if (element == null) {
            return;
        }
        if (element.getObjectValue() instanceof CacheElement) {
            CacheElement cacheElement = (CacheElement) element.getObjectValue();
            if (!cacheElement.isExpired()) {
                cacheElement.setExpired(true);
                expireElement(element, adjustTTL);
            }
        } else {
            expireElement(element, adjustTTL);
        }
    }

    private void expireElement(Element element, int adjustTTL) {
        long lastAccessTime = element.getLastAccessTime();
        long creationTime = element.getCreationTime();
        // set element eviction to ('now' + expiredTTL) seconds in the future
        int timeToLive = lastAccessTime == 0 ? expiredTTL : (int) (lastAccessTime - creationTime) / 1000 + expiredTTL;
        timeToLive += adjustTTL;
        element.setTimeToLive(timeToLive);
    }

    /*
     * Sets an element from DependencyCache to not expired. Updates its TTL.
     */
    private void setNotExpired(Element dependentElement) {
        if (dependentElement == null) {
            return;
        }
        CacheElement cacheElement = (CacheElement) dependentElement.getObjectValue();
        if (cacheElement.isExpired()) {
            cacheElement.setExpired(false);
            updateTTL(dependentElement);
        }
    }

    /*
     * Update TTL of element from DependencyCache to 'now' + cacheDependencyTTL
     * + adjustTTL
     */
    private void updateTTL(Element dependentElement) {
        if (dependentElement == null) {
            return;
        }
        long lastAccessTime = dependentElement.getLastAccessTime();
        long creationTime = dependentElement.getCreationTime();
        int timeToLive = lastAccessTime == 0 ? cacheDependencyTTL : (int) (lastAccessTime - creationTime) / 1000 +
                cacheDependencyTTL;
        timeToLive += ADJUST_TTL;
        dependentElement.setTimeToLive(timeToLive);
    }
}
package org.dd4t.caching.providers;

import org.dd4t.caching.CacheDependency;
import org.dd4t.caching.CacheElement;
import org.dd4t.caching.CacheInvalidator;
import org.dd4t.caching.impl.CacheElementImpl;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.providers.PayloadCacheProvider;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public class EHCache3Provider extends AbstractEHCacheProvider implements PayloadCacheProvider, CacheInvalidator {

    private static final Logger LOG = LoggerFactory.getLogger(EHCache3Provider.class);

    private static final String EHCACHE3_DD4T_XML = "/ehcache3-dd4t.xml";

    private static CacheManager cacheManager;

    static {
        final URL ehcacheConfig = EHCache3Provider.class.getResource(EHCACHE3_DD4T_XML);
        cacheManager = CacheManagerBuilder.
                newCacheManager(new XmlConfiguration(ehcacheConfig));
        cacheManager.init();
    }

    private final Cache<String, CacheElement> cache;

    private final Cache<String, CacheElement> dependencyCache;

    public EHCache3Provider() {
        cache = cacheManager.getCache(CACHE_NAME, String.class, CacheElement.class);
        dependencyCache = cacheManager.getCache(CACHE_NAME_DEPENDENCY, String.class, CacheElement.class);
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
    protected <T> void storeElement(final String key, final CacheElement<T> cacheElement) {

        if (!isEnabled()) {
            return;
        }

        if (cache.containsKey(key)) {
            cache.replace(key, cacheElement);
            LOG.debug("Replaced item with key:{} in cache.", key);
        } else {
            cache.put(key, cacheElement);
            LOG.debug("Added item with key:{} in cache.", key);
        }
    }

    @Override
    public void flush() {
        if (!isEnabled()) {
            return;
        }

        if (!cacheExists()) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        LOG.info("Expiring all items in cache");
        cache.clear();
        LOG.info("Expiring all items in dependency cache");
        dependencyCache.clear();
    }

    @Override
    public void invalidate(final String key) {
        if (!isEnabled()) {
            return;
        }

        if (!dependencyCacheExists()) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        String dependencyKey = getKey(key);
        if (!dependencyCache.containsKey(dependencyKey)) {
            LOG.debug("No dependency key found for key:{}. Doing nothing");
            return;
        }
        final CacheElement<ConcurrentSkipListSet<String>> dependentEntry = dependencyCache.get(dependencyKey);

        if (dependentEntry != null) {
            LOG.info("Expire key: {} from dependency cache", dependencyKey);
            ConcurrentSkipListSet<String> cacheSet = dependentEntry.getPayload();
            if (cacheSet != null) {
                for (String cacheKey : cacheSet) {
                    LOG.info("Removing dependent cache key: {} with item from main cache", cacheKey);
                    cache.remove(cacheKey);
                }
            }
        } else {
            LOG.info("Attempting to expire key {} but corresponding value not found in dependency cache",
                    dependencyKey);
        }
        dependencyCache.remove(dependencyKey);
        LOG.info("Removed dependency entry with key:{}", dependencyKey);
    }

    @Override
    public <T> CacheElement<T> loadPayloadFromLocalCache(final String key) {
        if (!isEnabled()) {
            LOG.debug("Cache is disabled. Returning a null Cache Element.");
            return new CacheElementImpl<>(null, true);
        }

        if (!doCheckForPreview() || (TridionUtils.getSessionPreviewToken() == null && cache != null)) {
            CacheElement<T> currentElement = cache.get(key);

            if (currentElement == null) {
                currentElement = new CacheElementImpl<>(null, true);
            }
            return currentElement;
        } else {
            LOG.debug("Disable cache for Preview Session Token: {}", TridionUtils.getSessionPreviewToken());
            return new CacheElementImpl<T>(null, true);
        }
    }

    @Override
    public <T> void storeInItemCache(final String key, final CacheElement<T> cacheElement, final
    List<CacheDependency> dependencies) {

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
            cacheElement.setExpired(true);
        }

        cacheElement.setExpired(false);

        if (cache.containsKey(key)) {
            cache.replace(key, cacheElement);
        } else {
            cache.put(key, cacheElement);
        }

        for (CacheDependency dep : dependencies) {
            String dependentKey = getKey(dep.getPublicationId(), dep.getItemId());
            cacheElement.setDependentKey(dependentKey);
            addDependency(key, dependentKey);
        }
    }

    /*
     * Makes the _fromKey_ dependent on _toKey_ It adds the _fromKey_ to the
     * list of values that depend on the _toKey_
     */
    @Override
    public void addDependency(final String cacheKey, final String dependencyKey) {

        if (!isEnabled()) {
            return;
        }

        if (!dependencyCacheExists()) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }

        LOG.debug("Add dependency from key: {} to key: {}", dependencyKey, cacheKey);

        if (dependencyCache.containsKey(dependencyKey)) {
            CacheElement<ConcurrentSkipListSet<String>> dependencyElement = dependencyCache.get(dependencyKey);

            if (dependencyElement != null && dependencyElement.getPayload() != null) {
                ConcurrentSkipListSet<String> cacheSet = dependencyElement.getPayload();

                if (!cacheSet.contains(cacheKey)) {
                    LOG.info("Adding cachekey: {} to dependent key: {}", cacheKey, dependencyKey);
                    cacheSet.add(cacheKey);
                }
                dependencyElement.setExpired(false);
                dependencyCache.replace(dependencyKey, dependencyElement);
            } else {
                addNewDependencyCacheElement(cacheKey, dependencyKey);
            }
        } else {
            addNewDependencyCacheElement(cacheKey, dependencyKey);
        }

        LOG.info("Added or replaced cache element with dependency key: {} and dependent key: {}", dependencyKey,
                cacheKey);
    }

    public Cache getCache() {
        return this.cache;
    }

    public Cache getDependencyCache() {
        return this.dependencyCache;
    }

    private void addNewDependencyCacheElement(final String cacheKey, final String dependencyKey) {
        LOG.info("Adding new dependency.");
        final ConcurrentSkipListSet<String> cacheSet = new ConcurrentSkipListSet<>();
        cacheSet.add(cacheKey);
        CacheElementImpl<ConcurrentSkipListSet<String>> cacheElement = new CacheElementImpl<>(cacheSet);
        cacheElement.setExpired(false);
        dependencyCache.put(dependencyKey, cacheElement);
    }
}
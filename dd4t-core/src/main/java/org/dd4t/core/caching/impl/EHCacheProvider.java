package org.dd4t.core.caching.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheInvalidator;
import org.dd4t.core.factories.impl.PropertiesServiceFactory;
import org.dd4t.core.services.PropertiesService;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.providers.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * EH Cache implementation
 *
 * TODO: this class has bugs! Only use for Tridion Object invalidation!
 *
 * @author R. Kempees, Mihai Cadariu
 */
public class EHCacheProvider implements CacheProvider, CacheInvalidator {

    /**
     * The name of the EHCache that contains the cached items for this application
     */
    public static final String CACHE_NAME = "DD4T";
    private final Cache cache = CacheManager.create().getCache(CACHE_NAME);
    public static final String DEPENDENT_KEY_FORMAT = "%s:%s";
    private static final Logger LOG = LoggerFactory.getLogger(EHCacheProvider.class);
	private final int expiredTTL;

    private EHCacheProvider () {
        LOG.debug("Create new instance");
        PropertiesServiceFactory propertiesServiceFactory = PropertiesServiceFactory.getInstance();
        PropertiesService propertiesService = propertiesServiceFactory.getPropertiesService();
	    final String ttlExpired = "cache.expired.ttl";
	    final String ttlExpiredDefault = "299";
	    String ttl = propertiesService.getProperty(ttlExpired, ttlExpiredDefault);
        expiredTTL = Integer.parseInt(ttl);
        LOG.debug("Using {} = {} seconds", ttlExpired, expiredTTL);
    }

    /**
     * Loads given object from the cache and returns it inside a CacheItem wrapper.
     *
     * @param key String representing the cache key to retrieve a payload for
     * @return CacheElement object wrapping the actual payload. Return object never null, but it can wrap a null
     * payload. It can also return an expired (stale) payload, which must be updated.
     */
    @Override
    public <T> CacheElement<T> loadFromLocalCache(String key) {
        String sessionPreviewToken = TridionUtils.getSessionPreviewToken();

        if (sessionPreviewToken == null && cache != null) {
            Element element = cache.get(key);

            if (element == null) {
                element = new Element(key, new CacheElementImpl<T>(null, true));
                setExpired(element);
                Element oldElement = cache.putIfAbsent(element);

                if (oldElement != null) {
                    element = oldElement;
                }
            }
            boolean isExpired = isExpired(element);
            CacheElement<T> cacheElement = (CacheElement<T>) element.getObjectValue();
            cacheElement.setExpired(isExpired);

            return cacheElement;
        } else {
            LOG.debug("Disable cache for Preview Session Token: {}", sessionPreviewToken);
            return new CacheElementImpl<T>(null, true);
        }
    }

    /**
     * Store given item in the cache with a simple time-to-live property.
     *
     * @param key          String representing the key to store the payload under
     * @param cacheElement CacheElement a wrapper around the actual value to store in cache
     */
    @Override
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement) {

        if (cache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }

        cacheElement.setExpired(false);
        Element element = new Element(key, cacheElement);

        if (cache.isKeyInCache(key)) {
            cache.replace(element);
        } else {
            cache.put(element);
        }
    }

    /**
     * Store given item in the cache with a reference to supplied Tridion Item.
     *
     * @param key                    String representing the key to store the cacheItem under
     * @param cacheElement           Object the actual value to store in cache
     * @param dependingPublicationId int representing the Publication id of the Tridion item the cacheItem depends on
     * @param dependingItemId        int representing the Item id of the Tridion item the cacheItem depends on
     */
    @Override
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement, int dependingPublicationId, int dependingItemId) {
        if (cache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }

        cacheElement.setExpired(false);
        Element element = cache.get(key);

        if (element == null) {
            cache.put(new Element(key, cacheElement, true));
        } else {
            setNotExpired(element);
        }

        String dependentKey = getKey(dependingPublicationId, dependingItemId);
        addDependency(key, dependentKey);
    }

    @Override
    public void flush() {
        if (cache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        LOG.debug("Expire all items in cache");
        for (Object key : cache.getKeys()) {
            setExpired(cache.get(key));
        }
    }

    @Override
    public void invalidate(final String key) {
        if (cache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        String fromKey = getKey(key);
        Element element = cache.get(fromKey);

        if (element != null) {
            LOG.debug("Expire key: {} from cache", fromKey);
            setExpired(element);
            ConcurrentSkipListSet<String> dependentSet = (ConcurrentSkipListSet<String>) element.getObjectValue();

            if (dependentSet != null) {
	            for (String dependentKey : dependentSet) {
		            LOG.debug("Expire dependent key: {} from cache", dependentKey);

		            Element dependentElement = cache.get(dependentKey);
		            setExpired(dependentElement);
	            }
            }
        }
    }

    /*
    Makes the _fromKey_ dependent on _toKey_
    It adds the _fromKey_ to the list of values that depend on the _toKey_
     */
    private void addDependency(String toKey, String fromKey) {
        if (cache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        ConcurrentSkipListSet<String> dependentSet = null;
        Element element = cache.get(fromKey);

        if (element != null) {
            dependentSet = (ConcurrentSkipListSet<String>) element.getObjectValue();
            setNotExpired(element);
        }

        if (dependentSet == null) {
            LOG.debug("Add empty dependencies for key: {} to cache", fromKey);
            dependentSet = new ConcurrentSkipListSet<>();
            Element dependentElement = new Element(fromKey, dependentSet, true);

            Element oldElement = cache.putIfAbsent(dependentElement);
            if (oldElement != null) {
                LOG.debug("Found existing dependencies for key: {} in cache", fromKey);
                dependentSet = (ConcurrentSkipListSet<String>) oldElement.getObjectValue();
            }
        }

        LOG.debug("Add dependency from key: {} to key: {}", fromKey, toKey);
        dependentSet.add(toKey);
    }

    private boolean isExpired(Element element) {
        return element == null || element.getTimeToLive() == expiredTTL;
    }

    private void setExpired(Element element) {
        if (element != null) {
            element.setTimeToLive(expiredTTL);
        }
    }

    private void setNotExpired(Element element) {
        if (element != null) {
            element.setEternal(true);
        }
    }

    private static String getKey(Serializable key) {
        String[] parts = ((String) key).split(":");
        switch (parts.length) {
            case 0:
                return "";

            case 1:
                return String.format(DEPENDENT_KEY_FORMAT, parts[0], "");

            default:
                return String.format(DEPENDENT_KEY_FORMAT, parts[0], parts[1]);
        }
    }

    private static String getKey(int publicationId, int itemId) {
        return String.format(DEPENDENT_KEY_FORMAT, publicationId, itemId);
    }
}

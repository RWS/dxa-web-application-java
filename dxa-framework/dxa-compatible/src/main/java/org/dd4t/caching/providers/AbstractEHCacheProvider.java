package org.dd4t.caching.providers;

import org.dd4t.caching.CacheDependency;
import org.dd4t.caching.CacheElement;
import org.dd4t.caching.CacheInvalidator;
import org.dd4t.caching.impl.CacheDependencyImpl;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public abstract class AbstractEHCacheProvider implements PayloadCacheProvider, CacheInvalidator {

    /**
     * The name of the EHCache that contains the cached items for this
     * application
     */
    protected static final String CACHE_NAME = "DD4T-Objects";

    protected static final String CACHE_NAME_DEPENDENCY = "DD4T-Dependencies";

    protected static final String DEPENDENT_KEY_FORMAT = "%s:%s";

    protected static final int ADJUST_TTL = 2;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEHCacheProvider.class);

    protected boolean checkForPreview = false;

    protected boolean isEnabled = true;


    protected static String getKey(Serializable key) {
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

    protected static String getKey(int publicationId, int itemId) {
        return String.format(DEPENDENT_KEY_FORMAT, publicationId, itemId);
    }

    protected boolean doCheckForPreview() {
        return checkForPreview;
    }

    protected void setCheckForPreview(boolean breakOnPreview) {
        this.checkForPreview = breakOnPreview;
    }

    /**
     * Store given item in the cache with a simple time-to-live property.
     *
     * @param key          String representing the key to store the payload under
     * @param cacheElement CacheElement a wrapper around the actual value to store in
     *                     cache
     */
    @Override
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement) {

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
        storeElement(key, cacheElement);
    }

    /**
     * Store given item in the cache with a reference to supplied Tridion Item.
     *
     * @param key                    String representing the key to store the cacheItem under
     * @param cacheElement           Object the actual value to store in cache
     * @param dependingPublicationId int representing the Publication id of the Tridion item the
     *                               cacheItem depends on
     * @param dependingItemId        int representing the Item id of the Tridion item the cacheItem
     *                               depends on
     */
    @Override
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement, int dependingPublicationId, int
            dependingItemId) {

        if (!isEnabled()) {
            return;
        }

        CacheDependency dependency = new CacheDependencyImpl(dependingPublicationId, dependingItemId);
        List<CacheDependency> dependencies = new ArrayList<>();
        dependencies.add(dependency);
        storeInItemCache(key, cacheElement, dependencies);

    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(final boolean enabled) {
        isEnabled = enabled;
    }

    protected abstract boolean cacheExists();

    protected abstract boolean dependencyCacheExists();

    protected abstract <T> void storeElement(final String key, final CacheElement<T> cacheElement);
}
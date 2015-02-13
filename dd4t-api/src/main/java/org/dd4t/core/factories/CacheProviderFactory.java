package org.dd4t.core.factories;

import org.dd4t.providers.PayloadCacheProvider;

/**
 * @author Mihai Cadariu
 */
public interface CacheProviderFactory {

    PayloadCacheProvider getCacheProvider();
}

package org.dd4t.core.factories;

import org.dd4t.providers.CacheProvider;

/**
 * @author Mihai Cadariu
 * @since 15.09.2014
 */
public interface CacheProviderFactory {

    CacheProvider getCacheProvider();
}

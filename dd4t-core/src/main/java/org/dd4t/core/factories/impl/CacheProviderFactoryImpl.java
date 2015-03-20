package org.dd4t.core.factories.impl;

import org.dd4t.core.factories.CacheProviderFactory;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mihai Cadariu
 */
public class CacheProviderFactoryImpl implements CacheProviderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CacheProviderFactoryImpl.class);
    private static final CacheProviderFactory INSTANCE = new CacheProviderFactoryImpl();
    private PayloadCacheProvider cacheProvider;

    private CacheProviderFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static CacheProviderFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public PayloadCacheProvider getCacheProvider() {
        return cacheProvider;
    }

    @Autowired
    private void setCacheProvider(PayloadCacheProvider cacheProvider) {
        LOG.debug("Set Cacheprovider: {} ",cacheProvider);
        this.cacheProvider = cacheProvider;
    }
}

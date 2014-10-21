package org.dd4t.core.factories.impl;

import org.dd4t.core.factories.CacheProviderFactory;
import org.dd4t.providers.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mihai Cadariu
 * @since 15.09.2014
 */
public class CacheProviderFactoryImpl implements CacheProviderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CacheProviderFactoryImpl.class);
    private static final CacheProviderFactory _instance = new CacheProviderFactoryImpl();
    private CacheProvider cacheProvider;

    private CacheProviderFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static CacheProviderFactory getInstance() {
        return _instance;
    }

    @Override
    public CacheProvider getCacheProvider() {
        return cacheProvider;
    }

    @Autowired
    private void setCacheProvider(CacheProvider cacheProvider) {
        LOG.debug("Set PublicationResolver " + cacheProvider);
        this.cacheProvider = cacheProvider;
    }
}

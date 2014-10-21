package org.dd4t.core.factories.impl;

import org.dd4t.providers.PageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PageProviderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PageProviderFactory.class);
    // Singleton implementation
    private static final PageProviderFactory _instance = new PageProviderFactory();
    @Autowired
    protected PageProvider pageProvider;

    protected PageProviderFactory() {
        LOG.debug("Create new instance");
    }

    public static PageProviderFactory getInstance() {
        return _instance;
    }

    public PageProvider getPageProvider() {
        return pageProvider;
    }
}
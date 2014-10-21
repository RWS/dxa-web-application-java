package org.dd4t.core.factories.impl;

import org.dd4t.core.resolvers.LinkResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * LinkResolverFactory. Hides actual LinkResolver implementations.
 * <p/>
 * Configured through Spring beans.
 *
 * @author R. Kempees
 * @since 10.06.2014
 */
public class LinkResolverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(LinkResolverFactory.class);
    private static final LinkResolverFactory _instance = new LinkResolverFactory();
    private LinkResolver linkResolver;

    private LinkResolverFactory() {
        LOG.debug("Create new instance");
    }

    public static LinkResolverFactory getInstance() {
        return _instance;
    }

    public LinkResolver getLinkResolver() {
        return linkResolver;
    }

    @Autowired
    private void setLinkResolver(LinkResolver linkResolver) {
        LOG.debug("Set LinkResolver " + linkResolver);
        this.linkResolver = linkResolver;
    }
}

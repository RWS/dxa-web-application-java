package org.dd4t.mvc.utils;

import org.dd4t.core.factories.PublicationResolverFactory;
import org.dd4t.core.resolvers.PublicationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PublicationResolverFactoryImpl hides actual implementation
 * Configured through Spring beans
 *
 * This is needed for allowing static access in PublicationUrl.java,
 * an EL function.
 *
 * @author Mihai Cadariu
 */
public class PublicationResolverFactoryImpl implements PublicationResolverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationResolverFactoryImpl.class);
    private static final PublicationResolverFactory INSTANCE = new PublicationResolverFactoryImpl();

	private PublicationResolver publicationResolver;

    private PublicationResolverFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static PublicationResolverFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public PublicationResolver getPublicationResolver() {
        return publicationResolver;
    }

    public void setPublicationResolver(PublicationResolver publicationResolver) {
        LOG.debug("Set PublicationResolver " + publicationResolver);
        this.publicationResolver = publicationResolver;
    }
}

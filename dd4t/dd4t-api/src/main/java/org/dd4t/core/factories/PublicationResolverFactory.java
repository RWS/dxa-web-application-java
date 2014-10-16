package org.dd4t.core.factories;

import org.dd4t.core.resolvers.PublicationResolver;

/**
 * @author Mihai Cadariu
 * @since 21.07.2014
 */
public interface PublicationResolverFactory {

    public PublicationResolver getPublicationResolver();
}

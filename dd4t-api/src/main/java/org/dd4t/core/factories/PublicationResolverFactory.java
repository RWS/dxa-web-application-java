package org.dd4t.core.factories;

import org.dd4t.core.resolvers.PublicationResolver;

/**
 * @author Mihai Cadariu
 */
public interface PublicationResolverFactory {

    public PublicationResolver getPublicationResolver();

	public void setPublicationResolver(PublicationResolver publicationResolver);
}

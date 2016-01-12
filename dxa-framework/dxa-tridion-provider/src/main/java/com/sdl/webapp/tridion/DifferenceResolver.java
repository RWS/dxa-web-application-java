package com.sdl.webapp.tridion;

import com.tridion.dynamiccontent.publication.PublicationMapping;

/**
 * There are some issues in a new CDaaS implementation, thus the old/new code are not compatible.
 * In order to resolve these issues, this interface is used. It should be implemented in all providers for particular
 * versions of CD.
 */
public interface DifferenceResolver {
    PublicationMapping getPublicationMappingFromUrl(String url);
}

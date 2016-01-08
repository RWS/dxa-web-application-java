package com.sdl.webapp.tridion;

import com.tridion.dynamiccontent.publication.PublicationMapping;

public interface DifferenceResolver {
    PublicationMapping getPublicationMappingFromUrl(String url);
}

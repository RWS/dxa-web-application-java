package com.sdl.webapp.tridion.versions;

import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.springframework.stereotype.Service;

/**
 * @deprecated should be removed once 2013SP1 support is dropped
 */
@Service
public class Web7Resolver implements TridionVersionsConflictResolver {

    @Override
    public PublicationMapping getPublicationMappingFromUrl(String url) {
        return DynamicContent.getInstance().getMappingsResolver().getPublicationMappingFromUrl(url);
    }
}

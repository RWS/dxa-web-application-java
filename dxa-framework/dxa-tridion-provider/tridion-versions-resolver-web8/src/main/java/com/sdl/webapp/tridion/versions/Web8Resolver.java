package com.sdl.webapp.tridion.versions;

import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.springframework.stereotype.Service;

/**
 * @deprecated should be removed once 2013SP1 support is dropped
 */
@Service
public class Web8Resolver implements TridionVersionsConflictResolver {

    @Override
    public PublicationMapping getPublicationMappingFromUrl(String url) {
        // dynamicMappingsRetriever.getPublicationMapping(UriUtils.encodePath(url, "UTF-8"));
        return null;
    }
}

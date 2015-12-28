package com.sdl.webapp.tridion;

import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.springframework.stereotype.Service;

/**
 * This interface is introduced to resolve differences between Web 2013SP1 and Web8
 * and should be removed once 2013SP1 support is dropped.
 * @deprecated should be only used exceptionally, and removed ASAP
 */
@Service
public class TridionVersionsConflictResolver {

    /**
     * Used in com.sdl.webapp.tridion.TridionLocalizationResolver of dxa-tridion-provider.
     * @param url URL-encoded url of a publication
     */
    public PublicationMapping getPublicationMappingFromUrl(String url) {
        return DynamicContent.getInstance().getMappingsResolver().getPublicationMappingFromUrl(url);
    }
}

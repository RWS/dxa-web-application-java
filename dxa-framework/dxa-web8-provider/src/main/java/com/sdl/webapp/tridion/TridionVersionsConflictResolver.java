package com.sdl.webapp.tridion;

import com.tridion.configuration.ConfigurationException;
import com.tridion.dynamiccontent.DynamicMappingsRetriever;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This interface is introduced to resolve differences between Web 2013SP1 and Web8
 * and should be removed once 2013SP1 support is dropped.
 * @deprecated should be only used exceptionally, and removed ASAP
 */
@Service
public class TridionVersionsConflictResolver {

    @Autowired
    private DynamicMappingsRetriever dynamicMappingsRetriever;

    /**
     * Used in com.sdl.webapp.tridion.TridionLocalizationResolver of dxa-tridion-provider.
     * @param url URL-encoded url of a publication
     */
    public PublicationMapping getPublicationMappingFromUrl(String url) {
        try {
            return dynamicMappingsRetriever.getPublicationMapping(url);
        } catch (ConfigurationException e) {
            return null;
        }
    }
}

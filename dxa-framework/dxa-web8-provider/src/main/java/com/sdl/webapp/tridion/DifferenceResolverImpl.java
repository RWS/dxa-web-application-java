package com.sdl.webapp.tridion;

import com.sdl.webapp.tridion.compatibility.DifferenceResolver;
import com.tridion.configuration.ConfigurationException;
import com.tridion.dynamiccontent.DynamicMappingsRetriever;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DifferenceResolverImpl implements DifferenceResolver {

    @Autowired
    private DynamicMappingsRetriever dynamicMappingsRetriever;

    @Override
    public PublicationMapping getPublicationMappingFromUrl(String url) {
        try {
            return dynamicMappingsRetriever.getPublicationMapping(url);
        } catch (ConfigurationException e) {
            return null;
        }
    }
}

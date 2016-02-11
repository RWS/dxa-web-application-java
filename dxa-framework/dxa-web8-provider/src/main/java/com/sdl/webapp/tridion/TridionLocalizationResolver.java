package com.sdl.webapp.tridion;

import com.tridion.configuration.ConfigurationException;
import com.tridion.dynamiccontent.DynamicMappingsRetriever;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/**
 * <p>TridionLocalizationResolver class.</p>
 */
public class TridionLocalizationResolver extends AbstractTridionLocalizationResolver {
    @Autowired
    private DynamicMappingsRetriever dynamicMappingsRetriever;

    /**
     * {@inheritDoc}
     */
    @Override
    protected PublicationMappingData getPublicationMappingData(String url) throws PublicationMappingNotFoundException {
        try {
            PublicationMapping publicationMapping = dynamicMappingsRetriever.getPublicationMapping(url);

            if (publicationMapping == null) {
                throw new PublicationMappingNotFoundException("Publication mapping not found. " +
                        "Check if your cd_dynamic_conf.xml configuration file contains a publication mapping " +
                        "that matches this URL: " + url);
            }

            return new PublicationMappingData(
                    String.valueOf(publicationMapping.getPublicationId()),
                    getPublicationMappingPath(publicationMapping.getPath()));
        } catch (ConfigurationException e) {
            return null;
        }
    }
}

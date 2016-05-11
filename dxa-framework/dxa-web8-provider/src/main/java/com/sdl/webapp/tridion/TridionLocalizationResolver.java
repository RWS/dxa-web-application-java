package com.sdl.webapp.tridion;

import com.sdl.web.api.dynamic.DynamicMappingsRetriever;
import com.sdl.web.api.dynamic.mapping.PublicationMapping;
import com.tridion.configuration.ConfigurationException;
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
            PublicationMapping publicationMapping = null;
            dynamicMappingsRetriever.getPublicationMapping(url);

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

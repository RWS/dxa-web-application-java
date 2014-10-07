package com.sdl.tridion.referenceimpl.common.config;

import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TridionConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TridionConfiguration.class);

    public Localization getLocalization(String url) {
        // TODO: Add caching mechanism so that we don't have to call the CD API and create a new Localization
        // for every request

        final PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver()
                .getPublicationMappingFromUrl(url);

        if (publicationMapping != null) {
            return Localization.newBuilder()
                    .setLocalizationId(publicationMapping.getPublicationId())
                    .setProtocol(publicationMapping.getProtocol())
                    .setDomain(publicationMapping.getDomain())
                    .setPort(publicationMapping.getPort())
                    .setPath(publicationMapping.getPath())
                    .build();
        }

        return null;
    }
}

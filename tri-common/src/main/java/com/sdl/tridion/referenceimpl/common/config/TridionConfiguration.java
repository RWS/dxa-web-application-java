package com.sdl.tridion.referenceimpl.common.config;

import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TridionConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TridionConfiguration.class);

    private Map<Integer, Localization> localizations = new HashMap<>();

    public Localization getLocalization(String url) {
        final PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver()
                .getPublicationMappingFromUrl(url);

        if (publicationMapping != null) {
            final int publicationId = publicationMapping.getPublicationId();

            Localization localization;
            synchronized (localizations) {
                localization = localizations.get(publicationId);
                if (localization == null) {
                    localization = Localization.newBuilder()
                            .setLocalizationId(publicationId)
                            .setProtocol(publicationMapping.getProtocol())
                            .setDomain(publicationMapping.getDomain())
                            .setPort(publicationMapping.getPort())
                            .setPath(publicationMapping.getPath())
                            .build();

                    LOG.debug("Created: {} -> {}", url, localization);
                    localizations.put(publicationId, localization);
                } else {
                    LOG.debug("Returning cached: {} -> {}", url, localization);
                }
            }

            return localization;
        }

        return null;
    }
}

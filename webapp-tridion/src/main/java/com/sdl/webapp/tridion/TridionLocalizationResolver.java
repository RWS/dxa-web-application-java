package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.*;
import com.sdl.webapp.common.impl.LocalizationImpl;
import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.sdl.webapp.tridion.PublicationMappingUtil.getPublicationMappingBaseUrl;
import static com.sdl.webapp.tridion.PublicationMappingUtil.getPublicationMappingPath;

/**
 * Implementation of {@code LocalizationResolver} that uses the Tridion API to determine the localization for a request.
 */
@Component
public class TridionLocalizationResolver implements LocalizationResolver {
    private static final Logger LOG = LoggerFactory.getLogger(TridionLocalizationResolver.class);

    private final Map<String, Localization> localizations = new HashMap<>();

    @Autowired
    private LocalizationConfigurationLoader localizationConfigurationLoader;

    @Override
    public Localization getLocalization(String url) throws LocalizationResolverException {
        final PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver()
                .getPublicationMappingFromUrl(url);
        if (publicationMapping == null) {
            throw new PublicationMappingNotFoundException("Publication mapping not found. " +
                    "Check if your cd_dynamic_conf.xml configuration file contains a publication mapping " +
                    "that matches this URL: " + url);
        }

        // Create a unique key consisting of publication ID and publication base URL
        final String key = String.format("%d@[%s]", publicationMapping.getPublicationId(),
                getPublicationMappingBaseUrl(publicationMapping));

        synchronized (localizations) {
            if (!localizations.containsKey(key)) {
                LOG.debug("Creating localization: {}", key);
                localizations.put(key, createLocalization(publicationMapping));
            }
        }

        return localizations.get(key);
    }

    private Localization createLocalization(PublicationMapping publicationMapping) throws LocalizationResolverException {
        final LocalizationImpl localization = new LocalizationImpl(
                Integer.toString(publicationMapping.getPublicationId()), getPublicationMappingPath(publicationMapping));

        try {
            localizationConfigurationLoader.loadConfiguration(localization);
        } catch (LocalizationConfigurationLoaderException e) {
            throw new LocalizationResolverException("Exception while reading configuration of localization: " +
                    localization, e);
        }

        return localization;
    }
}

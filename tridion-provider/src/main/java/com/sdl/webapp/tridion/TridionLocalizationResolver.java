package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.localization.*;
import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@code LocalizationResolver} that uses the Tridion API to determine the localization for a request.
 */
@Component
public class TridionLocalizationResolver implements LocalizationResolver {
    private static final Logger LOG = LoggerFactory.getLogger(TridionLocalizationResolver.class);

    private final Map<String, Localization> localizations = new HashMap<>();

    private final LocalizationFactory localizationFactory;

    @Autowired
    public TridionLocalizationResolver(LocalizationFactory localizationFactory) {
        this.localizationFactory = localizationFactory;
    }

    @Override
    public Localization getLocalization(String url) throws LocalizationResolverException {
        LOG.trace("getLocalization: {}", url);

        final PublicationMapping publicationMapping = getPublicationMappingFromUrl(url);
        if (publicationMapping == null) {
            throw new PublicationMappingNotFoundException("Publication mapping not found. " +
                    "Check if your cd_dynamic_conf.xml configuration file contains a publication mapping " +
                    "that matches this URL: " + url);
        }

        final String key = Integer.toString(publicationMapping.getPublicationId());

        synchronized (localizations) {
            if (!localizations.containsKey(key)) {
                localizations.put(key, createLocalization(publicationMapping));
            }
        }

        return localizations.get(key);
    }

    @Override
    public void refreshLocalization(String localizationId) {
        synchronized (localizations) {
            if (localizations.remove(localizationId) != null) {
                LOG.debug("Removed cached localization with id: {}", localizationId);
            }
        }
    }

    private PublicationMapping getPublicationMappingFromUrl(String url) {
        return DynamicContent.getInstance().getMappingsResolver().getPublicationMappingFromUrl(url);
    }

    private Localization createLocalization(PublicationMapping publicationMapping) throws LocalizationResolverException {
        final String id = Integer.toString(publicationMapping.getPublicationId());
        final String path = getPublicationMappingPath(publicationMapping);

        try {
            return localizationFactory.createLocalization(id, path);
        } catch (LocalizationFactoryException e) {
            throw new LocalizationResolverException("Exception while creating localization: [" + id + "] " + path, e);
        }
    }

    /**
     * Gets the publication mapping path. The returned path always starts with a "/" and does not end with a "/", unless
     * the path is the root path "/" itself.
     *
     * @param publicationMapping The publication mapping.
     * @return The publication mapping path.
     */
    private String getPublicationMappingPath(PublicationMapping publicationMapping) {
        String path = Strings.nullToEmpty(publicationMapping.getPath());
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}

package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.localization.LocalizationResolverException;
import com.sdl.webapp.tridion.compatibility.DifferenceResolver;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@code LocalizationResolver} that uses the Tridion API to determine the localization for a request.
 */
@Component
public class TridionLocalizationResolver implements LocalizationResolver {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);

    private final Map<String, Localization> localizations = Collections.synchronizedMap(new HashMap<String, Localization>());

    @Autowired
    private LocalizationFactory localizationFactory;

    @Autowired
    private DifferenceResolver differenceResolver;

    @Override
    public Localization getLocalization(String url) throws LocalizationResolverException {
        LOG.trace("getLocalization: {}", url);

        final PublicationMapping publicationMapping;
        try {
            publicationMapping = differenceResolver.getPublicationMappingFromUrl(UriUtils.encodePath(url, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Should never happen since UTF-8 is supported.");
        }
        if (publicationMapping == null) {
            throw new PublicationMappingNotFoundException("Publication mapping not found. " +
                    "Check if your cd_dynamic_conf.xml configuration file contains a publication mapping " +
                    "that matches this URL: " + url);
        }

        final String key = Integer.toString(publicationMapping.getPublicationId());

        if (!localizations.containsKey(key)) {
            localizations.put(key, createLocalization(publicationMapping));
        }

        return localizations.get(key);
    }

    @Override
    public boolean refreshLocalization(Localization localization) {
        if (localization == null) {
            return false;
        }
        String localizationId = localization.getId();
        if (localizations.remove(localizationId) != null) {
            LOG.debug("Removed cached localization with id: {}", localizationId);
            return true;
        }
        return false;
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

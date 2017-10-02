package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.sdl.web.api.dynamic.DynamicMappingsRetriever;
import com.sdl.web.api.dynamic.mapping.PublicationMapping;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.localization.LocalizationResolverException;
import com.tridion.configuration.ConfigurationException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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

    private static final Logger LOG = LoggerFactory.getLogger(TridionLocalizationResolver.class);

    private final Map<String, Localization> localizations = Collections.synchronizedMap(new HashMap<String, Localization>());

    @Autowired
    private LocalizationFactory localizationFactory;

    @Autowired
    private DynamicMappingsRetriever dynamicMappingsRetriever;

    /**
     * Gets the publication mapping path. The returned path always starts with a "/" and does not end with a "/", unless
     * the path is the root path "/" itself.
     *
     * @param originalPath The publication mapping original path
     * @return The publication mapping path.
     */
    protected static String getPublicationMappingPath(String originalPath) {
        String path = Strings.nullToEmpty(originalPath);
        if (!path.startsWith("/")) {
            path = '/' + path;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SneakyThrows(UnsupportedEncodingException.class)
    public Localization getLocalization(String url) throws LocalizationResolverException {
        LOG.trace("getLocalization: {}", url);

        // truncating on first % because of TSI-1281
        String path = UriUtils.encodePath(url, "UTF-8").split("%")[0];
        PublicationMappingData data = getPublicationMappingData(path);

        if (data == null) {
            throw new LocalizationResolverException("Publication mapping is not resolved!");
        }

        if (!localizations.containsKey(data.id)) {
            localizations.put(data.id, createLocalization(data.id, data.path));
        }

        return localizations.get(data.id);
    }

    /**
     * {@inheritDoc}
     */
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
            LOG.error("Configuration exception", e);
            return null;
        }
    }

    private Localization createLocalization(String id, String path) throws LocalizationResolverException {
        try {
            return localizationFactory.createLocalization(id, path);
        } catch (LocalizationFactoryException e) {
            throw new LocalizationResolverException("Exception while creating localization: [" + id + "] " + path, e);
        }
    }

    @AllArgsConstructor
    protected static class PublicationMappingData {
        protected String id, path;
    }
}

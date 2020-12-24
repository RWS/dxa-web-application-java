package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.PublicationMapping;
import com.sdl.web.pca.client.exception.ApiClientException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.localization.LocalizationResolverException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Sites;

/**
 * Implementation of {@code LocalizationResolver} that uses the Api Client to determine the localization for a request.
 */
@Component
@Profile("!cil.providers.active")
public class GraphQLLocalizationResolver implements LocalizationResolver {

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLLocalizationResolver.class);

    private final Map<String, Localization> localizations = Collections.synchronizedMap(new HashMap<>());

    private LocalizationFactory localizationFactory;

    private ApiClient apiClient;

    public GraphQLLocalizationResolver() {
    }

    @Autowired
    public GraphQLLocalizationResolver(LocalizationFactory localizationFactory, ApiClientProvider apiClientProvider) {
        this.localizationFactory = localizationFactory;
        this.apiClient = apiClientProvider.getClient();
    }

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
        if (localizations.containsKey(url)) {
            return localizations.get(url);
        }
        // truncating on first % because of TSI-1281
        String path = UriUtils.encodePath(url, "UTF-8").split("%")[0];
        PublicationMappingData data = getPublicationMappingData(path);

        if (data == null) {
            throw new LocalizationResolverException("Publication mapping is not resolved for URL: " + url);
        }

        Localization localization = createLocalization(data.id, data.path);
        localizations.putIfAbsent(url, localization);

        return localization;
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
        Set<String> toRemove = new HashSet<>();
        for (Map.Entry<String, Localization> entry : localizations.entrySet()) {
            String id = entry.getValue().getId();
            if (id != null && id.equals(localizationId)) {
                toRemove.add(entry.getKey());
                LOG.debug("Found cached localization with id: {} and url: {}",
                        localizationId, entry.getKey());
            }
        }
        for (String idToRemove : toRemove) {
            if (localizations.remove(idToRemove) != null) {
                LOG.debug("Removed cached localization with id: {}", localizationId);
                return true;
            }
        }
        return false;
    }

    protected PublicationMappingData getPublicationMappingData(String url) throws PublicationMappingNotFoundException {
        try {
            // Publication Mapping is more specific to Tridion Sites,
            // hence Tridion Sites is passed which is similar to .NET implementation
            PublicationMapping publicationMapping = apiClient.getPublicationMapping(Sites, url);

            if (publicationMapping == null) {
                throw new PublicationMappingNotFoundException("Publication mapping not found. " +
                        "There is not any publication mapping that matches this URL: " + url);
            }

            int pubId = publicationMapping.getPublicationId();
            String path = getPublicationMappingPath(publicationMapping.getPath());
            return new PublicationMappingData(String.valueOf(pubId), path);
        } catch (ApiClientException ex) {
            throw new PublicationMappingNotFoundException("Cannot fetch publication mapping for URL: " + url, ex);
        }
    }

    private Localization createLocalization(String id, String path) throws LocalizationResolverException {
        try {
            return localizationFactory.createLocalization(id, path);
        } catch (LocalizationFactoryException e) {
            throw new LocalizationResolverException("Could not create a localization for pubId: [" + id +
                    "] and path: [" + path + "]", e);
        }
    }

    @AllArgsConstructor
    protected static class PublicationMappingData {
        protected String id, path;
    }
}

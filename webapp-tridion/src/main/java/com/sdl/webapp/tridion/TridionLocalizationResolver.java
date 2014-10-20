package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.Localization;
import com.sdl.webapp.common.api.LocalizationResolver;
import com.sdl.webapp.common.api.LocalizationResolverException;
import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TridionLocalizationResolver implements LocalizationResolver {
    private static final Logger LOG = LoggerFactory.getLogger(TridionLocalizationResolver.class);

    private Map<String, Localization> localizations = new HashMap<>();

    @Override
    public Localization getLocalization(String url) throws LocalizationResolverException {
        final PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver()
                .getPublicationMappingFromUrl(url);
        if (publicationMapping == null) {
            throw new PublicationMappingNotFoundException("Publication mapping not found. " +
                    "Check if your cd_dynamic_conf.xml configuration file contains a publication mapping " +
                    "that matches this URL: " + url);
        }

        // TODO: Are localization IDs unique? If not, then there is a problem here.

        final String localizationId = Integer.toString(publicationMapping.getPublicationId());
        if (!localizations.containsKey(localizationId)) {
            localizations.put(localizationId, createLocalization(publicationMapping));
        }

        return localizations.get(localizationId);
    }

    private Localization createLocalization(PublicationMapping publicationMapping) {
        final String localizationId = Integer.toString(publicationMapping.getPublicationId());

        return new Localization() {
            @Override
            public String getId() {
                return localizationId;
            }

            @Override
            public String getPath() {
                // TODO: Implement this method
                throw new UnsupportedOperationException("Not yet implemented");
            }

            @Override
            public boolean isStaticContent(String url) {
                // TODO: Implement this method
                throw new UnsupportedOperationException("Not yet implemented");
            }

            @Override
            public String getResource(String key) {
                // TODO: Implement this method
                throw new UnsupportedOperationException("Not yet implemented");
            }

            @Override
            public List<String> getIncludes(String pageTypeId) {
                // TODO: Implement this method
                throw new UnsupportedOperationException("Not yet implemented");
            }
        };
    }
}

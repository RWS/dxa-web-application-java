package com.sdl.webapp.tridion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.*;
import com.sdl.webapp.common.impl.LocalizationImpl;
import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.sdl.webapp.tridion.PublicationMappingUtil.getPublicationMappingBaseUrl;
import static com.sdl.webapp.tridion.PublicationMappingUtil.getPublicationMappingPath;

/**
 * Implementation of {@code LocalizationResolver} that uses the Tridion API to determine the localization for a request.
 */
@Component
public class TridionLocalizationResolver implements LocalizationResolver {
    private static final Logger LOG = LoggerFactory.getLogger(TridionLocalizationResolver.class);

    private static final String CONFIG_BOOTSTRAP_PATH = "/system/config/_all.json";
    private static final String RESOURCES_BOOTSTRAP_PATH = "/system/resources/_all.json";
    private static final String INCLUDES_PATH = "/system/mappings/includes.json";

    private static final String MEDIA_ROOT_NODE_NAME = "mediaRoot";
    private static final String DEFAULT_LOCALIZATION_NODE_NAME = "defaultLocalization";
    private static final String STAGING_NODE_NAME = "staging";
    private static final String FILES_NODE_NAME = "files";

    private final Map<String, Localization> localizations = new HashMap<>();

    @Autowired
    private StaticContentProvider contentProvider;

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

        final JsonNode configRootNode = parseJsonFile(CONFIG_BOOTSTRAP_PATH, localization);
        localization.setMediaRoot(configRootNode.get(MEDIA_ROOT_NODE_NAME).asText());
        localization.setDefault(configRootNode.get(DEFAULT_LOCALIZATION_NODE_NAME).asBoolean(false));
        localization.setStaging(configRootNode.get(STAGING_NODE_NAME).asBoolean(false));
        localization.setConfiguration(parseJsonSubFiles(configRootNode, localization));

        final JsonNode resourcesRootNode = parseJsonFile(RESOURCES_BOOTSTRAP_PATH, localization);
        localization.setResources(parseJsonSubFiles(resourcesRootNode, localization));

        final JsonNode includesRootNode = parseJsonFile(INCLUDES_PATH, localization);
        final ListMultimap<String, String> includes = ArrayListMultimap.create();
        final Iterator<Map.Entry<String, JsonNode>> i = includesRootNode.fields();
        while (i.hasNext()) {
            final Map.Entry<String, JsonNode> entry = i.next();
            final String pageTypeId = entry.getKey();
            for (JsonNode value : entry.getValue()) {
                includes.put(pageTypeId, value.asText());
            }
        }
        localization.setIncludes(includes);

        return localization;
    }

    private JsonNode parseJsonFile(String path, Localization localization) throws LocalizationResolverException {
        try (final InputStream in = contentProvider.getStaticContent(path, localization).getContent()) {
            return new ObjectMapper().readTree(in);
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationResolverException("Exception while reading configuration of localization: " +
                    localization, e);
        }
    }

    private Map<String, String> parseJsonSubFiles(JsonNode rootNode, Localization localization)
            throws LocalizationResolverException {
        final Map<String, String> map = new HashMap<>();

        final JsonNode filesNode = rootNode.get(FILES_NODE_NAME);
        if (filesNode != null) {
            for (JsonNode subFileNode : filesNode) {
                final String subFilePath = subFileNode.asText();
                if (!Strings.isNullOrEmpty(subFilePath)) {
                    final String prefix = subFilePath.substring(subFilePath.lastIndexOf('/') + 1,
                            subFilePath.lastIndexOf('.') + 1);

                    final Iterator<Map.Entry<String, JsonNode>> i = parseJsonFile(subFilePath, localization).fields();
                    while (i.hasNext()) {
                        final Map.Entry<String, JsonNode> entry = i.next();
                        map.put(prefix + entry.getKey(), entry.getValue().asText());
                    }
                }
            }
        }

        return map;
    }
}

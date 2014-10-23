package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.ContentProviderException;
import com.sdl.webapp.common.api.StaticContentItem;
import com.sdl.webapp.common.api.StaticContentProvider;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class LocalizationFactoryImpl implements LocalizationFactory {
    private static final Logger LOG = LoggerFactory.getLogger(LocalizationFactoryImpl.class);

    private static final String CONFIG_BOOTSTRAP_PATH = "/system/config/_all.json";
    private static final String RESOURCES_BOOTSTRAP_PATH = "/system/resources/_all.json";

    private static final String SEMANTIC_SCHEMAS_PATH = "/system/mappings/schemas.json";
    private static final String SEMANTIC_VOCABULARIES_PATH = "/system/mappings/vocabularies.json";

    private static final String INCLUDES_PATH = "/system/mappings/includes.json";

    private static final String MEDIA_ROOT_NODE_NAME = "mediaRoot";
    private static final String DEFAULT_LOCALIZATION_NODE_NAME = "defaultLocalization";
    private static final String STAGING_NODE_NAME = "staging";
    private static final String FILES_NODE_NAME = "files";

    private static final String DEFAULT_MEDIA_ROOT = "/media/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StaticContentProvider contentProvider;

    @Override
    public Localization createLocalization(String id, String path) throws LocalizationFactoryException {
        LOG.debug("createLocalization: [{}] {}", id, path);

        final LocalizationImpl localization = new LocalizationImpl(id, path);

        loadMainConfiguration(localization);
        loadResources(localization);
        loadSemanticSchemas(localization);
        loadSemanticVocabularies(localization);
        loadIncludes(localization);

        return localization;
    }

    private void loadMainConfiguration(LocalizationImpl localization) throws LocalizationFactoryException {
        final JsonNode configRootNode = parseJsonFile(CONFIG_BOOTSTRAP_PATH, localization);
        localization.setMediaRoot(configRootNode.get(MEDIA_ROOT_NODE_NAME).asText(DEFAULT_MEDIA_ROOT));
        localization.setDefault(configRootNode.get(DEFAULT_LOCALIZATION_NODE_NAME).asBoolean(false));
        localization.setStaging(configRootNode.get(STAGING_NODE_NAME).asBoolean(false));
        localization.setConfiguration(parseJsonSubFiles(configRootNode, localization));
    }

    private void loadResources(LocalizationImpl localization) throws LocalizationFactoryException {
        final JsonNode resourcesRootNode = parseJsonFile(RESOURCES_BOOTSTRAP_PATH, localization);
        localization.setResources(parseJsonSubFiles(resourcesRootNode, localization));
    }

    private void loadSemanticSchemas(LocalizationImpl localization) throws LocalizationFactoryException {
        try {
            final StaticContentItem schemasItem = contentProvider.getStaticContent(SEMANTIC_SCHEMAS_PATH,
                    localization.getId(), localization.getPath());

            final List<SemanticSchema> schemas;
            try (final InputStream in = schemasItem.getContent()) {
                schemas = objectMapper.readValue(in, new TypeReference<List<SemanticSchema>>() { });
            }

            final ImmutableMap.Builder<Long, SemanticSchema> builder = ImmutableMap.builder();
            for (SemanticSchema schema : schemas) {
                builder.put(schema.getId(), schema);
            }

            localization.setSemanticSchemas(builder.build());
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading semantic schema configuration of " +
                    "localization: " + localization, e);
        }
    }

    private void loadSemanticVocabularies(LocalizationImpl localization) throws LocalizationFactoryException {
        try {
            final StaticContentItem vocabulariesItem = contentProvider.getStaticContent(SEMANTIC_VOCABULARIES_PATH,
                    localization.getId(), localization.getPath());

            final List<SemanticVocabulary> vocabularies;
            try (final InputStream in = vocabulariesItem.getContent()) {
                vocabularies = objectMapper.readValue(in, new TypeReference<List<SemanticVocabulary>>() { });
            }

            localization.setSemanticVocabularies(vocabularies);
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading semantic vocabulary configuration of " +
                    "localization: " + localization, e);
        }
    }

    private void loadIncludes(LocalizationImpl localization) throws LocalizationFactoryException {
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
    }

    private JsonNode parseJsonFile(String path, Localization localization) throws LocalizationFactoryException {
        try {
            final StaticContentItem staticContentItem = contentProvider.getStaticContent(path,
                    localization.getId(), localization.getPath());

            try (final InputStream in = staticContentItem.getContent()) {
                return objectMapper.readTree(in);
            }
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: " +
                    localization, e);
        }
    }

    private Map<String, String> parseJsonSubFiles(JsonNode rootNode, Localization localization)
            throws LocalizationFactoryException {
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

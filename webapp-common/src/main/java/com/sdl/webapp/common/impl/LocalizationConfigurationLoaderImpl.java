package com.sdl.webapp.common.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.*;
import com.sdl.webapp.common.api.mapping.SemanticSchema;
import com.sdl.webapp.common.api.mapping.SemanticVocabulary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class LocalizationConfigurationLoaderImpl implements LocalizationConfigurationLoader {

    private static final String CONFIG_BOOTSTRAP_PATH = "/system/config/_all.json";
    private static final String RESOURCES_BOOTSTRAP_PATH = "/system/resources/_all.json";

    private static final String SEMANTIC_SCHEMAS_PATH = "/system/mappings/schemas.json";
    private static final String SEMANTIC_VOCABULARIES_PATH = "/system/mappings/vocabularies.json";

    private static final String INCLUDES_PATH = "/system/mappings/includes.json";

    private static final String MEDIA_ROOT_NODE_NAME = "mediaRoot";
    private static final String DEFAULT_LOCALIZATION_NODE_NAME = "defaultLocalization";
    private static final String STAGING_NODE_NAME = "staging";
    private static final String FILES_NODE_NAME = "files";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StaticContentProvider contentProvider;

    @Override
    public void loadConfiguration(LocalizationImpl localization) throws LocalizationConfigurationLoaderException {
        loadMainConfiguration(localization);
        loadResources(localization);
        loadSemanticSchemas(localization);
        loadSemanticVocabularies(localization);
        loadIncludes(localization);
    }

    private void loadMainConfiguration(LocalizationImpl localization) throws LocalizationConfigurationLoaderException {
        final JsonNode configRootNode = parseJsonFile(CONFIG_BOOTSTRAP_PATH, localization);
        localization.setMediaRoot(configRootNode.get(MEDIA_ROOT_NODE_NAME).asText());
        localization.setDefault(configRootNode.get(DEFAULT_LOCALIZATION_NODE_NAME).asBoolean(false));
        localization.setStaging(configRootNode.get(STAGING_NODE_NAME).asBoolean(false));
        localization.setConfiguration(parseJsonSubFiles(configRootNode, localization));
    }

    private void loadResources(LocalizationImpl localization) throws LocalizationConfigurationLoaderException {
        final JsonNode resourcesRootNode = parseJsonFile(RESOURCES_BOOTSTRAP_PATH, localization);
        localization.setResources(parseJsonSubFiles(resourcesRootNode, localization));
    }

    private void loadSemanticSchemas(LocalizationImpl localization) throws LocalizationConfigurationLoaderException {
        try {
            final List<SemanticSchema> schemas = objectMapper.readValue(
                    contentProvider.getStaticContent(SEMANTIC_SCHEMAS_PATH, localization).getContent(),
                    new TypeReference<List<SemanticSchema>>() { });

            final ImmutableMap.Builder<Long, SemanticSchema> builder = ImmutableMap.builder();
            for (SemanticSchema schema : schemas) {
                builder.put(schema.getId(), schema);
            }

            localization.setSemanticSchemas(builder.build());
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationConfigurationLoaderException("Exception while reading semantic schema " +
                    "configuration of localization: " + localization, e);
        }
    }

    private void loadSemanticVocabularies(LocalizationImpl localization) throws LocalizationConfigurationLoaderException {
        try {
            final List<SemanticVocabulary> vocabularies = objectMapper.readValue(
                    contentProvider.getStaticContent(SEMANTIC_VOCABULARIES_PATH, localization).getContent(),
                    new TypeReference<List<SemanticVocabulary>>() {
                    });

            localization.setSemanticVocabularies(vocabularies);
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationConfigurationLoaderException("Exception while reading semantic vocabulary " +
                    "configuration of localization: " + localization, e);
        }
    }

    private void loadIncludes(LocalizationImpl localization) throws LocalizationConfigurationLoaderException {
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



    private JsonNode parseJsonFile(String path, Localization localization)
            throws LocalizationConfigurationLoaderException {
        try (final InputStream in = contentProvider.getStaticContent(path, localization).getContent()) {
            return objectMapper.readTree(in);
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationConfigurationLoaderException("Exception while reading configuration of " +
                    "localization: " + localization, e);
        }
    }

    private Map<String, String> parseJsonSubFiles(JsonNode rootNode, Localization localization)
            throws LocalizationConfigurationLoaderException {
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

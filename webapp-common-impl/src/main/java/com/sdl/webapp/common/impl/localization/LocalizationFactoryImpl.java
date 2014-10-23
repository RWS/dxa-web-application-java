package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentProvider;
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

/**
 * Implementation of {@code LocalizationFactory}.
 */
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

        final LocalizationImpl.Builder builder = LocalizationImpl.newBuilder()
                .setId(id)
                .setPath(path);

        loadMainConfiguration(id, path, builder);
        loadResources(id, path, builder);
        loadSemanticSchemas(id, path, builder);
        loadSemanticVocabularies(id, path, builder);
        loadIncludes(id, path, builder);

        final Localization localization = builder.build();
        LOG.trace("Created localization: {}", localization);

        return localization;
    }

    private void loadMainConfiguration(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        final JsonNode configRootNode = parseJsonFile(CONFIG_BOOTSTRAP_PATH, id, path);
        builder.setMediaRoot(configRootNode.get(MEDIA_ROOT_NODE_NAME).asText(DEFAULT_MEDIA_ROOT))
                .setDefault(configRootNode.get(DEFAULT_LOCALIZATION_NODE_NAME).asBoolean(false))
                .setStaging(configRootNode.get(STAGING_NODE_NAME).asBoolean(false))
                .addConfiguration(parseJsonSubFiles(configRootNode, id, path));
    }

    private void loadResources(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        final JsonNode resourcesRootNode = parseJsonFile(RESOURCES_BOOTSTRAP_PATH, id, path);
        builder.addResources(parseJsonSubFiles(resourcesRootNode, id, path));
    }

    private void loadSemanticSchemas(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        try {
            final StaticContentItem item = contentProvider.getStaticContent(SEMANTIC_SCHEMAS_PATH, id, path);

            final List<SemanticSchema> semanticSchemas;
            try (final InputStream in = item.getContent()) {
                semanticSchemas = objectMapper.readValue(in, new TypeReference<List<SemanticSchema>>() { });
            }

            builder.addSemanticSchemas(semanticSchemas);
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading semantic schema configuration of " +
                    "localization: [" + id + "] " + path, e);
        }
    }

    private void loadSemanticVocabularies(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        try {
            final StaticContentItem item = contentProvider.getStaticContent(SEMANTIC_VOCABULARIES_PATH, id, path);

            final List<SemanticVocabulary> semanticVocabularies;
            try (final InputStream in = item.getContent()) {
                semanticVocabularies = objectMapper.readValue(in, new TypeReference<List<SemanticVocabulary>>() { });
            }

            builder.addSemanticVocabularies(semanticVocabularies);
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading semantic vocabulary configuration of " +
                    "localization: [" + id + "] " + path, e);
        }
    }

    private void loadIncludes(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        final JsonNode includesRootNode = parseJsonFile(INCLUDES_PATH, id, path);
        final Iterator<Map.Entry<String, JsonNode>> i = includesRootNode.fields();
        while (i.hasNext()) {
            final Map.Entry<String, JsonNode> entry = i.next();
            final String pageTypeId = entry.getKey();
            for (JsonNode value : entry.getValue()) {
                builder.addInclude(pageTypeId, value.asText());
            }
        }
    }

    private JsonNode parseJsonFile(String filePath, String localizationId, String localizationPath)
            throws LocalizationFactoryException {
        try {
            final StaticContentItem staticContentItem = contentProvider.getStaticContent(filePath, localizationId,
                    localizationPath);

            try (final InputStream in = staticContentItem.getContent()) {
                return objectMapper.readTree(in);
            }
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" +
                    localizationId + "] " + localizationPath, e);
        }
    }

    private Map<String, String> parseJsonSubFiles(JsonNode rootNode, String localizationId, String localizationPath)
            throws LocalizationFactoryException {
        final Map<String, String> map = new HashMap<>();

        final JsonNode filesNode = rootNode.get(FILES_NODE_NAME);
        if (filesNode != null) {
            for (JsonNode subFileNode : filesNode) {
                final String subFilePath = subFileNode.asText();
                if (!Strings.isNullOrEmpty(subFilePath)) {
                    final String prefix = subFilePath.substring(subFilePath.lastIndexOf('/') + 1,
                            subFilePath.lastIndexOf('.') + 1);

                    final Iterator<Map.Entry<String, JsonNode>> i = parseJsonFile(subFilePath, localizationId,
                            localizationPath).fields();
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

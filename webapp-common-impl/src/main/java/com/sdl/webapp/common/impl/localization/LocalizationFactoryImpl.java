package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.sdl.webapp.common.api.content.StaticContentProvider;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.impl.localization.semantics.JsonSchema;
import com.sdl.webapp.common.impl.localization.semantics.JsonVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sdl.webapp.common.impl.localization.JsonFileUtils.*;
import static com.sdl.webapp.common.impl.localization.semantics.SemanticsConverter.convertSemantics;

/**
 * Implementation of {@code LocalizationFactory}.
 *
 * This factory creates {@code Localization} instances and loads configuration information for each localization.
 * The configuration of a localization is stored in a number of JSON files that are retrieved via the static content
 * provider.
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

    private static final String DEFAULT_MEDIA_ROOT = "/media/";

    private final StaticContentProvider staticContentProvider;

    @Autowired
    public LocalizationFactoryImpl(StaticContentProvider staticContentProvider) {
        this.staticContentProvider = staticContentProvider;
    }

    @Override
    public Localization createLocalization(String id, String path) throws LocalizationFactoryException {
        LOG.debug("createLocalization: [{}] {}", id, path);

        final LocalizationImpl.Builder builder = LocalizationImpl.newBuilder()
                .setId(id)
                .setPath(path);

        loadMainConfiguration(id, path, builder);
        loadResources(id, path, builder);

        final List<JsonSchema> semanticSchemas = parseJsonFileObject(staticContentProvider,
                SEMANTIC_SCHEMAS_PATH, id, path, new TypeReference<List<JsonSchema>>() { });

        final List<JsonVocabulary> semanticVocabularies = parseJsonFileObject(staticContentProvider,
                SEMANTIC_VOCABULARIES_PATH, id, path, new TypeReference<List<JsonVocabulary>>() { });

        builder.addSemanticSchemas(convertSemantics(semanticSchemas, semanticVocabularies));

        loadIncludes(id, path, builder);

        final Localization localization = builder.build();
        LOG.trace("Created localization: {}", localization);

        return localization;
    }

    private void loadMainConfiguration(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        final JsonNode configRootNode = parseJsonFileTree(staticContentProvider, CONFIG_BOOTSTRAP_PATH, id, path);
        builder.setMediaRoot(configRootNode.get(MEDIA_ROOT_NODE_NAME).asText(DEFAULT_MEDIA_ROOT))
                .setDefault(configRootNode.get(DEFAULT_LOCALIZATION_NODE_NAME).asBoolean(false))
                .setStaging(configRootNode.get(STAGING_NODE_NAME).asBoolean(false))
                .addConfiguration(parseJsonSubFiles(staticContentProvider, configRootNode, id, path));
    }

    private void loadResources(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        final JsonNode resourcesRootNode = parseJsonFileTree(staticContentProvider, RESOURCES_BOOTSTRAP_PATH, id, path);
        builder.addResources(parseJsonSubFiles(staticContentProvider, resourcesRootNode, id, path));
    }

    private void loadIncludes(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        final JsonNode includesRootNode = parseJsonFileTree(staticContentProvider, INCLUDES_PATH, id, path);
        final Iterator<Map.Entry<String, JsonNode>> i = includesRootNode.fields();
        while (i.hasNext()) {
            final Map.Entry<String, JsonNode> entry = i.next();
            final String pageTypeId = entry.getKey();
            for (JsonNode value : entry.getValue()) {
                builder.addInclude(pageTypeId, value.asText());
            }
        }
    }
}

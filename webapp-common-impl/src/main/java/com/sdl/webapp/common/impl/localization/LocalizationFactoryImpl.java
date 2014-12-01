package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
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
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    private static final String VERSION_PATH = "/version.json";
    private static final String DEFAULT_VERSION_PATH = "/system/assets/version.json";

    private static final String SEMANTIC_SCHEMAS_PATH = "/system/mappings/schemas.json";
    private static final String SEMANTIC_VOCABULARIES_PATH = "/system/mappings/vocabularies.json";

    private static final String INCLUDES_PATH = "/system/mappings/includes.json";

    private static final String MEDIA_ROOT_NODE_NAME = "mediaRoot";
    private static final String DEFAULT_LOCALIZATION_NODE_NAME = "defaultLocalization";
    private static final String SITE_LOCALIZATIONS_NODE_NAME = "siteLocalizations";
    private static final String STAGING_NODE_NAME = "staging";

    private static final String DEFAULT_MEDIA_ROOT = "/media/";

    private static final String FILES_NODE_NAME = "files";

    private final StaticContentProvider staticContentProvider;

    private final WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper;

    @Autowired
    public LocalizationFactoryImpl(StaticContentProvider staticContentProvider,
                                   WebApplicationContext webApplicationContext, ObjectMapper objectMapper) {
        this.staticContentProvider = staticContentProvider;
        this.webApplicationContext = webApplicationContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public Localization createLocalization(String id, String path) throws LocalizationFactoryException {
        LOG.debug("createLocalization: [{}] {}", id, path);

        final LocalizationImpl.Builder builder = LocalizationImpl.newBuilder()
                .setId(id)
                .setPath(path);

        loadMainConfiguration(id, path, builder);
        loadVersion(id, path, builder);
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
                .addSiteLocalizations(loadSiteLocalizations(configRootNode))
                .addConfiguration(parseJsonSubFiles(staticContentProvider, configRootNode, id, path));
    }

    private List<SiteLocalizationImpl> loadSiteLocalizations(JsonNode configRootNode) {
        return objectMapper.convertValue(configRootNode.get(SITE_LOCALIZATIONS_NODE_NAME),
                new TypeReference<List<SiteLocalizationImpl>>() { });
    }

    private void loadVersion(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {
        try {
            StaticContentItem item = staticContentProvider.getStaticContent(VERSION_PATH, id, path);
            try (final InputStream in = item.getContent()) {
                builder.setVersion(objectMapper.readTree(in).get("version").asText());
                return;
            }
        } catch (StaticContentNotFoundException e) {
            LOG.debug("No published version.json found for localization [{}] {}", id, path);
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + id +
                    "] " + path, e);
        }

        final File file = new File(new File(webApplicationContext.getServletContext().getRealPath("/")),
                DEFAULT_VERSION_PATH);
        if (!file.exists()) {
            throw new LocalizationFactoryException("File not found: " + file.getPath());
        }

        try (final InputStream in = new FileInputStream(file)) {
            builder.setVersion(objectMapper.readTree(in).get("version").asText());
        } catch (IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + id +
                    "] " + path, e);
        }
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

    public <T> T parseJsonFileObject(StaticContentProvider staticContentProvider, String filePath, String locId,
                                            String locPath, TypeReference<T> resultType)
            throws LocalizationFactoryException {
        try {
            final StaticContentItem item = staticContentProvider.getStaticContent(filePath, locId, locPath);
            try (final InputStream in = item.getContent()) {
                return objectMapper.readValue(in, resultType);
            }
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + locId +
                    "] " + locPath, e);
        }
    }

    public JsonNode parseJsonFileTree(StaticContentProvider staticContentProvider, String filePath, String locId,
                                             String locPath)
            throws LocalizationFactoryException {
        try {
            final StaticContentItem item = staticContentProvider.getStaticContent(filePath, locId, locPath);
            try (final InputStream in = item.getContent()) {
                return objectMapper.readTree(in);
            }
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + locId +
                    "] " + locPath, e);
        }
    }

    public Map<String, String> parseJsonSubFiles(StaticContentProvider staticContentProvider, JsonNode rootNode,
                                                        String locId, String locPath)
            throws LocalizationFactoryException {
        final Map<String, String> map = new HashMap<>();

        final JsonNode filesNode = rootNode.get(FILES_NODE_NAME);
        if (filesNode != null) {
            for (JsonNode subFileNode : filesNode) {
                final String subFilePath = subFileNode.asText();
                if (!Strings.isNullOrEmpty(subFilePath)) {
                    final String prefix = subFilePath.substring(subFilePath.lastIndexOf('/') + 1,
                            subFilePath.lastIndexOf('.') + 1);

                    final Iterator<Map.Entry<String, JsonNode>> i = parseJsonFileTree(staticContentProvider,
                            subFilePath, locId,  locPath).fields();
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

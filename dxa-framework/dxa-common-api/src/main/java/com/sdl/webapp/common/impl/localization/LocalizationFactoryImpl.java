package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.impl.localization.semantics.JsonSchema;
import com.sdl.webapp.common.impl.localization.semantics.JsonVocabulary;
import com.sdl.webapp.common.util.InitializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sdl.webapp.common.impl.localization.semantics.SemanticsConverter.convertSemantics;

/**
 * Implementation of {@code LocalizationFactory}.
 * <p>
 * This factory creates {@code Localization} instances and loads configuration information for each localization.
 * The configuration of a localization is stored in a number of JSON files that are retrieved via the static content
 * provider.
 * </p>
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

    @Autowired
    private ContentProvider contentProvider;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Localization createLocalization(String id, String path) throws LocalizationFactoryException {
        LOG.debug("createLocalization: [{}] {}", id, path);

        LocalizationImpl.Builder builder = LocalizationImpl.newBuilder()
                .setId(id)
                .setPath(path);

        loadMainConfiguration(id, path, builder);
        loadVersion(id, path, builder);
        loadResources(id, path, builder);

        List<JsonSchema> semanticSchemas = parseJsonFileObject(contentProvider,
                SEMANTIC_SCHEMAS_PATH, id, path, new TypeReference<List<JsonSchema>>() {
                });

        List<JsonVocabulary> semanticVocabularies = parseJsonFileObject(contentProvider,
                SEMANTIC_VOCABULARIES_PATH, id, path, new TypeReference<List<JsonVocabulary>>() {
                });

        List<SemanticSchema> schemas = convertSemantics(semanticSchemas, semanticVocabularies);
        SemanticSchema semanticSchema = getTopicSchema();
        schemas.add(semanticSchema);

        builder.addSemanticSchemas(schemas);

        loadIncludes(id, path, builder);

        Localization localization = builder.build();
        LOG.info("Localization: " + localization + " is created");

        return localization;
    }

    /**
     * This method creates specific Schema for Topics that are published from Docs and don't have any schema in Sites
     *
     * @return SemanticSchema
     */
    SemanticSchema getTopicSchema() {
        return new SemanticSchema(1, null, new HashSet<>(), getSemanticMappings());
    }

    Map<FieldSemantics, SemanticField> getSemanticMappings() {
        Map<FieldSemantics, SemanticField> result = new HashMap<>();
        String rootElementName = "Topic";
        String topicTitleElem = "topicTitle";
        String topicBodyElem = "topicBody";

        SemanticField titleSF = new SemanticField("title", String.format("/%s/%s", rootElementName, topicTitleElem), false, Collections.EMPTY_MAP);
        SemanticField topicSF = new SemanticField("topic", String.format("/%s/%s", rootElementName, topicBodyElem), false, Collections.EMPTY_MAP);

        result.put(new FieldSemantics(SemanticVocabulary.SDL_CORE_VOCABULARY, rootElementName, topicTitleElem), titleSF);
        result.put(new FieldSemantics(SemanticVocabulary.SDL_CORE_VOCABULARY, rootElementName, topicBodyElem), topicSF);

        return result;
    }

    private void loadMainConfiguration(String id,
                                       String path,
                                       LocalizationImpl.Builder builder) throws LocalizationFactoryException {
        JsonNode configRootNode = parseJsonFileTree(contentProvider, CONFIG_BOOTSTRAP_PATH, id, path);
        String mediaRoot = getTextFromConfig(configRootNode, MEDIA_ROOT_NODE_NAME, DEFAULT_MEDIA_ROOT);
        boolean isDefault = getBooleanFromConfig(configRootNode, DEFAULT_LOCALIZATION_NODE_NAME, false);
        boolean isStaging = getBooleanFromConfig(configRootNode, STAGING_NODE_NAME,  false);
        List<SiteLocalizationImpl> siteLocalizations = loadSiteLocalizations(configRootNode);
        Map<String, String> configuration = parseJsonSubFiles(contentProvider, configRootNode, id, path);
        builder.setMediaRoot(mediaRoot)
                .setDefault(isDefault)
                .setStaging(isStaging)
                .addSiteLocalizations(siteLocalizations)
                .addConfiguration(configuration);
    }

    private String getTextFromConfig(JsonNode configRootNode, String nodeName, String defaultValue) {
        if (configRootNode == null) {
            LOG.debug("Could not find a config, returning {} by default", defaultValue);
            return defaultValue;
        }
        JsonNode jsonNode = configRootNode.get(nodeName);
        if (jsonNode == null) {
            LOG.debug("Could not find a node '{}' within config '{}', returning {} by default",
                    nodeName, configRootNode.asText(), defaultValue);
            return defaultValue;
        }
        String result = jsonNode.asText(defaultValue);
        LOG.debug("Found a node '{}' within config '{}', returning {}",
                nodeName, configRootNode.asText(), result);
        return result;
    }

    private boolean getBooleanFromConfig(JsonNode configRootNode, String nodeName, boolean defaultValue) {
        if (configRootNode == null) {
            LOG.debug("Could not find a config, returning {} by default", defaultValue);
            return defaultValue;
        }
        JsonNode jsonNode = configRootNode.get(nodeName);
        if (jsonNode == null) {
            LOG.debug("Could not find a node '{}' within config '{}', returning {} by default",
                    nodeName, configRootNode.asText(), defaultValue);
            return defaultValue;
        }
        boolean result = jsonNode.asBoolean(defaultValue);
        LOG.debug("Found a node '{}' within config '{}', returning {}",
                nodeName, configRootNode.asText(), result);
        return result;
    }

    private List<SiteLocalizationImpl> loadSiteLocalizations(JsonNode configRootNode) {
        return objectMapper.convertValue(configRootNode.get(SITE_LOCALIZATIONS_NODE_NAME),
                new TypeReference<List<SiteLocalizationImpl>>() {
                });
    }

    private boolean loadVersionFromProperties(String id, String path, LocalizationImpl.Builder builder) {
        String assetsVersion = InitializationUtils.loadDxaProperties().getProperty("dxa.assets.version");
        if (Strings.isNullOrEmpty(assetsVersion)) {
            return false;
        }
        builder.setVersion(assetsVersion);
        builder.setHtmlDesignPublished(false);
        return true;
    }

    private boolean loadVersionFromBroker(String id, String path, LocalizationImpl.Builder builder) throws LocalizationFactoryException {
        try {
            StaticContentItem item = contentProvider.getStaticContent(VERSION_PATH, id, path);
            try (InputStream in = item.getContent()) {
                builder.setVersion(objectMapper.readTree(in).get("version").asText());
                builder.setHtmlDesignPublished(true);
                return true;
            }
        }
        catch (StaticContentNotFoundException e) {
            LOG.error("No published version.json found for localization [{}] in path {}", id, path, e);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + id +
                    "] " + path, e);
        }
        return false;
    }

    private boolean loadVersionFromWebapp(String id,
                                          String path,
                                          LocalizationImpl.Builder builder) throws LocalizationFactoryException {
        File file = new File(new File(webApplicationContext.getServletContext().getRealPath("/")), DEFAULT_VERSION_PATH);
        if (!file.exists()) {
            throw new LocalizationFactoryException("File not found: " + file.getPath());
        }
        try (InputStream in = new FileInputStream(file)) {
            builder.setVersion(objectMapper.readTree(in).get("version").asText());
            builder.setHtmlDesignPublished(false);
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + id +
                    "] " + path, e);
        }
    }

    private void loadVersion(String id, String path, LocalizationImpl.Builder builder)
            throws LocalizationFactoryException {

        // first, try to load the current asset version from the dxa.properties file.
        // if that is not found, try to load from the broker version.json file, or finally from the web app version.json file
        if (loadVersionFromProperties(id, path, builder)) {
            LOG.trace("Version: {} loaded from properties for id: {}", builder.getVersion(), id);
            return;
        }
        if (loadVersionFromBroker(id, path, builder)) {
            LOG.trace("Version: {} loaded from broker for id: {}", builder.getVersion(), id);
            return;
        }
        if (loadVersionFromWebapp(id, path, builder)) {
            LOG.trace("Version: {}} loaded from webapp for id: {}", builder.getVersion(), id);
            return;
        }
        LOG.info("Version is not loaded at all for id: {}", id);
    }

    private void loadResources(String id,
                               String path,
                               LocalizationImpl.Builder builder) throws LocalizationFactoryException {
        JsonNode resourcesRootNode = parseJsonFileTree(contentProvider, RESOURCES_BOOTSTRAP_PATH, id, path);
        builder.addResources(parseJsonSubFiles(contentProvider, resourcesRootNode, id, path));
    }

    private void loadIncludes(String id,
                              String path,
                              LocalizationImpl.Builder builder) throws LocalizationFactoryException {
        JsonNode includesRootNode = parseJsonFileTree(contentProvider, INCLUDES_PATH, id, path);
        Iterator<Map.Entry<String, JsonNode>> i = includesRootNode.fields();
        while (i.hasNext()) {
            Map.Entry<String, JsonNode> entry = i.next();
            String pageTypeId = entry.getKey();
            for (JsonNode value : entry.getValue()) {
                builder.addInclude(pageTypeId, value.asText());
            }
        }
    }

    /**
     * <p>parseJsonFileObject.</p>
     *
     * @param contentProvider a {@link ContentProvider} object.
     * @param filePath        a {@link String} object.
     * @param locId           a {@link String} object.
     * @param locPath         a {@link String} object.
     * @param resultType      a {@link TypeReference} object.
     * @param <T>             a T object.
     * @return a T object.
     * @throws LocalizationFactoryException if any.
     */
    public <T> T parseJsonFileObject(ContentProvider contentProvider,
                                     String filePath,
                                     String locId,
                                     String locPath,
                                     TypeReference<T> resultType) throws LocalizationFactoryException {
        try {
            StaticContentItem item = contentProvider.getStaticContent(filePath, locId, locPath);
            try (InputStream in = item.getContent()) {
                return objectMapper.readValue(in, resultType);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + locId +
                    "] " + locPath + " for file [" + filePath + "]", e);
        }
    }

    /**
     * <p>parseJsonFileTree.</p>
     *
     * @param contentProvider a {@link ContentProvider} object.
     * @param filePath a {@link String} object.
     * @param locId a {@link String} object.
     * @param locPath a {@link String} object.
     * @return a {@link JsonNode} object.
     * @throws LocalizationFactoryException if any.
     */
    public JsonNode parseJsonFileTree(ContentProvider contentProvider,
                                      String filePath,
                                      String locId,
                                      String locPath) throws LocalizationFactoryException {
        try {
            StaticContentItem item = contentProvider.getStaticContent(filePath, locId, locPath);
            try (InputStream in = item.getContent()) {
                return objectMapper.readTree(in);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LocalizationFactoryException("Could not read configuration of localization for pubId: [" + locId +
                    "] and path [" + locPath + "] for file [" + filePath + "]", e);
        }
    }

    /**
     * <p>parseJsonSubFiles.</p>
     *
     * @param contentProvider a {@link ContentProvider} object.
     * @param rootNode a {@link JsonNode} object.
     * @param locId a {@link String} object.
     * @param locPath a {@link String} object.
     * @return a {@link Map} object.
     * @throws LocalizationFactoryException if any.
     */
    public Map<String, String> parseJsonSubFiles(ContentProvider contentProvider,
                                                 JsonNode rootNode,
                                                 String locId,
                                                 String locPath) throws LocalizationFactoryException {
        Map<String, String> map = new HashMap<>();
        JsonNode filesNode = rootNode.get(FILES_NODE_NAME);
        if (filesNode == null) {
            return map;
        }
        for (JsonNode subFileNode : filesNode) {
            String subFilePath = subFileNode.asText();
            if (Strings.isNullOrEmpty(subFilePath)) {
                continue;
            }
            String prefix = subFilePath.substring(subFilePath.lastIndexOf('/') + 1, subFilePath.lastIndexOf('.') + 1);
            Iterator<Map.Entry<String, JsonNode>> i = parseJsonFileTree(contentProvider, subFilePath, locId, locPath).fields();
            while (i.hasNext()) {
                Map.Entry<String, JsonNode> entry = i.next();
                LOG.debug("Subfile: {}", prefix + entry.getKey());
                map.put(prefix + entry.getKey(), entry.getValue().asText());
            }
        }
        return map;
    }
}
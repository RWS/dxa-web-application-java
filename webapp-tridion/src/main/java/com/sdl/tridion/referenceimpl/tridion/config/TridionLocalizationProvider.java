package com.sdl.tridion.referenceimpl.tridion.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.sdl.tridion.referenceimpl.common.StaticContentProvider;
import com.sdl.tridion.referenceimpl.common.config.Localization;
import com.sdl.tridion.referenceimpl.common.config.LocalizationProvider;
import com.sdl.tridion.referenceimpl.common.config.WebAppContext;
import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of {@code LocalizationProvider} that gets information from the Tridion configuration.
 * It uses the CD API to get configuration information from {@code cd_dynamic_conf.xml}.
 */
@Component
public class TridionLocalizationProvider implements LocalizationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TridionLocalizationProvider.class);

    private static final String BOOTSTRAP_JSON_URL = "/system/_all.json";
    private static final String MAIN_CONFIG_URL = "/system/config/_all.json";
    private static final String MAIN_RESOURCES_URL = "/system/resources/_all.json";
    private static final String INCLUDES_URL = "/system/mappings/includes.json";

    private static final String FILES_NODE_NAME = "files";
    private static final String MEDIA_ROOT_NODE_NAME = "mediaRoot";

    @Autowired
    private WebAppContext webAppContext;

    @Autowired
    private StaticContentProvider staticContentProvider;

    private Map<Integer, Localization> localizations = new HashMap<>();

    @Override
    public Localization getLocalization(String url) throws IOException {
        final PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver()
                .getPublicationMappingFromUrl(url);
        return publicationMapping != null ? getLocalization(publicationMapping) : null;
    }

    private synchronized Localization getLocalization(PublicationMapping publicationMapping) throws IOException {
        final int publicationId = publicationMapping.getPublicationId();
        if (!localizations.containsKey(publicationId)) {
            localizations.put(publicationId, createLocalization(publicationMapping));
        }

        return localizations.get(publicationId);
    }

    private Localization createLocalization(PublicationMapping publicationMapping) throws IOException {
        final int publicationId = publicationMapping.getPublicationId();
        LOG.debug("Creating localization for publication: {}", publicationId);

        final String localizationPath = publicationMapping.getPath();
        final File baseDir = new File(new File(webAppContext.getStaticsPath(), Integer.toString(publicationId)), localizationPath);

        fetchJsonFiles(BOOTSTRAP_JSON_URL, baseDir, publicationId);

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode configRootNode = objectMapper.readTree(new File(baseDir, MAIN_CONFIG_URL));
        final JsonNode resourcesRootNode = objectMapper.readTree(new File(baseDir, MAIN_RESOURCES_URL));
        final JsonNode includesRootNode = objectMapper.readTree(new File(baseDir, INCLUDES_URL));

        final Localization localization = new Localization(publicationId, localizationPath,
                configRootNode.get(MEDIA_ROOT_NODE_NAME).asText(),
                parseJsonFiles(configRootNode, baseDir),
                parseJsonFiles(resourcesRootNode, baseDir),
                parseJsonIncludes(includesRootNode));
        LOG.debug("Created: {}", localization);

        return localization;
    }

    private void fetchJsonFiles(String url, File baseDir, int publicationId) throws IOException {
        final File file = new File(baseDir, url);
        if (!staticContentProvider.getStaticContent(url, file, publicationId)) {
            throw new FileNotFoundException("Configuration file not found: " + url);
        }

        // Recursively fetch referenced configuration files
        final JsonNode filesNode = new ObjectMapper().readTree(file).get(FILES_NODE_NAME);
        if (filesNode != null && filesNode.getNodeType() == JsonNodeType.ARRAY) {
            for (JsonNode subFileNode : filesNode) {
                final String subFileUrl = subFileNode.asText();
                if (!Strings.isNullOrEmpty(subFileUrl)) {
                    fetchJsonFiles(subFileUrl, baseDir, publicationId);
                }
            }
        }
    }

    private Map<String, String> parseJsonFiles(JsonNode configRootNode, File baseDir) throws IOException {
        final Map<String, String> properties = new HashMap<>();

        final JsonNode filesNode = configRootNode.get(FILES_NODE_NAME);
        if (filesNode != null && filesNode.getNodeType() == JsonNodeType.ARRAY) {
            for (JsonNode subFileNode : filesNode) {
                final String subFileUrl = subFileNode.asText();
                if (!Strings.isNullOrEmpty(subFileUrl)) {
                    final String prefix = subFileUrl.substring(subFileUrl.lastIndexOf('/') + 1,
                            subFileUrl.lastIndexOf('.') + 1);

                    final JsonNode rootNode = new ObjectMapper().readTree(new File(baseDir, subFileUrl));
                    final Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.fields();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonNode> entry = iterator.next();
                        properties.put(prefix + entry.getKey(), entry.getValue().asText());
                    }
                }
            }
        }

        return properties;
    }

    private ListMultimap<String, String> parseJsonIncludes(JsonNode includesRootNode) throws IOException {
        ListMultimap<String, String> includes = ArrayListMultimap.create();

        final Iterator<Map.Entry<String, JsonNode>> iterator = includesRootNode.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            final String pageTypeId = entry.getKey();
            for (JsonNode value : entry.getValue()) {
                includes.put(pageTypeId, value.asText());
            }
        }

        return includes;
    }
}

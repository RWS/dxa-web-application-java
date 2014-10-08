package com.sdl.tridion.referenceimpl.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Strings;
import com.sdl.tridion.referenceimpl.common.StaticFileManager;
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
import java.util.Map;

@Component
public class TridionLocalizationProvider implements LocalizationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TridionLocalizationProvider.class);

    private static final String ROOT_CONFIG_URL = "/system/_all.json";
    private static final String MAIN_CONFIG_URL = "/system/config/_all.json";

    private static final String FILES_NODE_NAME = "files";
    private static final String MEDIA_ROOT_NODE_NAME = "mediaRoot";

    @Autowired
    private SiteConfiguration siteConfiguration;

    @Autowired
    private StaticFileManager staticFileManager;

    private Map<Integer, Localization> localizations = new HashMap<>();

    @Override
    public Localization getLocalization(String url) throws IOException {
        final PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver()
                .getPublicationMappingFromUrl(url);
        return publicationMapping != null ? getLocalization(publicationMapping) : null;
    }

    private synchronized Localization getLocalization(PublicationMapping publicationMapping) throws IOException {
        final int publicationId = publicationMapping.getPublicationId();
        if (localizations.containsKey(publicationId)) {
            return localizations.get(publicationId);
        }

        LOG.debug("Creating localization for publication: {}", publicationId);
        final String localizationPath = publicationMapping.getPath();

        final File baseDir = new File(siteConfiguration.getStaticsPath(), localizationPath);
        getJsonConfig(ROOT_CONFIG_URL, baseDir, publicationId);

        final JsonNode configRootNode = new ObjectMapper().readTree(new File(baseDir, MAIN_CONFIG_URL));
        final String mediaRoot = configRootNode.get(MEDIA_ROOT_NODE_NAME).asText();

        final Localization localization = new Localization(publicationId, localizationPath, mediaRoot);
        LOG.debug("Created: {}", localization);

        localizations.put(publicationId, localization);
        return localization;
    }

    private void getJsonConfig(String url, File baseDir, int publicationId) throws IOException {
        final File file = new File(baseDir, url);
        if (!staticFileManager.getStaticContent(url, file, publicationId)) {
            throw new FileNotFoundException("Configuration file not found: " + url);
        }

        final JsonNode filesNode = new ObjectMapper().readTree(file).get(FILES_NODE_NAME);
        if (filesNode != null && filesNode.getNodeType() == JsonNodeType.ARRAY) {
            for (int i = 0; i < filesNode.size(); i++) {
                final String subFileUrl = filesNode.get(i).asText();
                if (!Strings.isNullOrEmpty(subFileUrl)) {
                    getJsonConfig(subFileUrl, baseDir, publicationId);
                }
            }
        }
    }
}

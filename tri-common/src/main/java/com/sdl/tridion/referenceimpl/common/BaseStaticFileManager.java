package com.sdl.tridion.referenceimpl.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Strings;
import com.sdl.tridion.referenceimpl.common.config.SiteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

public abstract class BaseStaticFileManager implements StaticFileManager {
    private static final Logger LOG = LoggerFactory.getLogger(BaseStaticFileManager.class);

    @Autowired
    private SiteConfiguration siteConfiguration;

    @Override
    public String createStaticAssets(File baseDirectory) throws IOException {
        getJsonConfig(baseDirectory, "_all.json");
        return "v1.00";
    }

    private void getJsonConfig(File baseDirectory, String filePath) throws IOException {
        LOG.debug("getJsonConfig: baseDirectory={}, filePath={}", baseDirectory, filePath);

        final String url = siteConfiguration.getSystemDir() + filePath;
        final File file = new File(baseDirectory, url);
        getStaticContent(url, file);

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode rootNode = objectMapper.readTree(file);
        final JsonNode filesNode = rootNode.get("files");
        if (filesNode != null && filesNode.getNodeType() == JsonNodeType.ARRAY) {
            for (int i = 0; i < filesNode.size(); i++) {
                final String subFilePath = filesNode.get(i).asText();
                if (!Strings.isNullOrEmpty(subFilePath)) {
                    getJsonConfig(baseDirectory, subFilePath);
                }
            }
        }
    }
}

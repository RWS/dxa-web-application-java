package com.sdl.tridion.referenceimpl.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class BaseStaticFileManager implements StaticFileManager {
    private static final Logger LOG = LoggerFactory.getLogger(BaseStaticFileManager.class);

    private static final String JSON_CONFIG_ROOT = "/system/_all.json";

    @Override
    public String createStaticAssets(File baseDirectory) throws IOException {
        getJsonConfig(baseDirectory, JSON_CONFIG_ROOT);
        return "v1.00";
    }

    private void getJsonConfig(File baseDirectory, String url) throws IOException {
        LOG.debug("getJsonConfig: baseDirectory={}, url={}", baseDirectory, url);

        final File file = new File(baseDirectory, url);
        if (getStaticContent(url, file)) {
            final JsonNode filesNode = new ObjectMapper().readTree(file).get("files");
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
}

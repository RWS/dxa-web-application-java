package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentProvider;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class JsonFileUtils {

    private static final String FILES_NODE_NAME = "files";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonFileUtils() {
    }

    public static <T> T parseJsonFileObject(StaticContentProvider staticContentProvider, String filePath, String locId,
                                            String locPath, TypeReference<T> resultType)
            throws LocalizationFactoryException {
        try {
            final StaticContentItem item = staticContentProvider.getStaticContent(filePath, locId, locPath);
            try (final InputStream in = item.getContent()) {
                return OBJECT_MAPPER.readValue(in, resultType);
            }
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + locId +
                    "] " + locPath, e);
        }
    }

    public static JsonNode parseJsonFileTree(StaticContentProvider staticContentProvider, String filePath, String locId,
                                             String locPath)
            throws LocalizationFactoryException {
        try {
            final StaticContentItem item = staticContentProvider.getStaticContent(filePath, locId, locPath);
            try (final InputStream in = item.getContent()) {
                return OBJECT_MAPPER.readTree(in);
            }
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + locId +
                    "] " + locPath, e);
        }
    }

    public static Map<String, String> parseJsonSubFiles(StaticContentProvider staticContentProvider, JsonNode rootNode,
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

package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentProvider;
import com.sdl.webapp.common.api.localization.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class XpmRegionConfig {
    private static final Logger LOG = LoggerFactory.getLogger(XpmRegionConfig.class);

    private static final String REGIONS_PATH = "/system/mappings/regions.json";

    private final Map<String, Map<String, XpmRegion>> regionsByLocalization = new HashMap<>();

    private final StaticContentProvider staticContentProvider;

    private final ObjectMapper objectMapper;

    @Autowired
    public XpmRegionConfig(StaticContentProvider staticContentProvider, ObjectMapper objectMapper) {
        this.staticContentProvider = staticContentProvider;
        this.objectMapper = objectMapper;
    }

    public synchronized XpmRegion getXpmRegion(String regionName, Localization localization) {
        final String localizationId = localization.getId();
        if (!regionsByLocalization.containsKey(localizationId)) {
            final Map<String, XpmRegion> regionsByName = new HashMap<>();
            for (XpmRegion region : loadXpmRegions(localization)) {
                regionsByName.put(region.getRegionName(), region);
            }
            regionsByLocalization.put(localizationId, regionsByName);
        }

        return regionsByLocalization.get(localizationId).get(regionName);
    }

    private List<XpmRegion> loadXpmRegions(Localization localization) {
        final StaticContentItem item;
        try {
            item = staticContentProvider.getStaticContent(REGIONS_PATH, localization.getId(), localization.getPath());
        } catch (ContentProviderException e) {
            LOG.error("Exception while reading XPM regions configuration", e);
            return null;
        }

        try (final InputStream in = item.getContent()) {
            return objectMapper.readValue(in, new TypeReference<List<XpmRegion>>() { });
        } catch (IOException e) {
            LOG.error("Exception while reading XPM regions configuration", e);
            return null;
        }
    }
}

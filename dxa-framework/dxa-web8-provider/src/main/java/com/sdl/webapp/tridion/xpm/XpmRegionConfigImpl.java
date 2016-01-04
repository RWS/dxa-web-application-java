package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.xpm.ComponentType;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
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
public class XpmRegionConfigImpl implements XpmRegionConfig {
    private static final Logger LOG = LoggerFactory.getLogger(XpmRegionConfigImpl.class);

    private static final String REGIONS_PATH = "/system/mappings/regions.json";

    private final Map<String, Map<String, XpmRegion>> regionsByLocalization = new HashMap<>();

    private final ContentProvider contentProvider;

    private final ObjectMapper objectMapper;

    @Autowired
    public XpmRegionConfigImpl(ContentProvider contentProvider, ObjectMapper objectMapper) {
        this.contentProvider = contentProvider;
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
            item = contentProvider.getStaticContent(REGIONS_PATH, localization.getId(), localization.getPath());
        } catch (ContentProviderException e) {
            LOG.error("Exception while reading XPM regions configuration", e);
            return null;
        }

        try (final InputStream in = item.getContent()) {
            SimpleModule module = new SimpleModule("ComponentTypeMapper", Version.unknownVersion());
            module.addAbstractTypeMapping(ComponentType.class, ComponentTypeImpl.class);
            objectMapper.registerModule(module); // important, otherwise won't have any effect on mapper's configuration
            return objectMapper.readValue(in, new TypeReference<List<XpmRegionImpl>>() {
            });
        } catch (IOException e) {
            LOG.error("Exception while reading XPM regions configuration", e);
            return null;
        }
    }
}

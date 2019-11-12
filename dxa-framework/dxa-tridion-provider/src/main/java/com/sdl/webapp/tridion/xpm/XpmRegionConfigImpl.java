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
import com.sdl.webapp.common.api.xpm.OccurrenceConstraint;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
import com.sdl.webapp.common.exceptions.DxaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
/**
 * <p>XpmRegionConfigImpl class.</p>
 */
public class XpmRegionConfigImpl implements XpmRegionConfig {
    private static final Logger LOG = LoggerFactory.getLogger(XpmRegionConfigImpl.class);

    private static final String REGIONS_PATH = "/system/mappings/regions.json";
    private static final int TOTAL_SIZE_TO_START_LOGGING = 100;

    private final ConcurrentHashMap<String, Map<String, XpmRegion>> regionsByLocalization = new ConcurrentHashMap<>();

    private final ContentProvider contentProvider;

    private final ObjectMapper objectMapper;

    /**
     * <p>Constructor for XpmRegionConfigImpl.</p>
     *
     * @param contentProvider a {@link com.sdl.webapp.common.api.content.ContentProvider} object.
     * @param objectMapper    a {@link com.fasterxml.jackson.databind.ObjectMapper} object.
     */
    @Autowired
    public XpmRegionConfigImpl(ContentProvider contentProvider,
                               ObjectMapper objectMapper) {
        this.contentProvider = contentProvider;
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    public XpmRegion getXpmRegion(String regionName, Localization localization) {
        Map<String, XpmRegion> regions = regionsByLocalization.get(localization.getId());
        if (regions != null) {
            return regions.get(regionName);
        }
        final Map<String, XpmRegion> newRegionsByName = new HashMap<>();
        List<XpmRegion> xpmRegions = loadXpmRegions(localization);
        for (XpmRegion region : xpmRegions) {
            newRegionsByName.put(region.getRegionName(), region);
        }
        Map<String, XpmRegion> oldRegionsByName = regionsByLocalization.putIfAbsent(localization.getId(), newRegionsByName);
        if (oldRegionsByName == null) oldRegionsByName = newRegionsByName;
        if (LOG.isDebugEnabled() && regionsByLocalization.size() > TOTAL_SIZE_TO_START_LOGGING) {
            LOG.debug("RegionsByLocalization cache contains " + regionsByLocalization.size() +
                    " different localizations with " + oldRegionsByName.size() +
                    " regions for " + localization.getId());
        }
        return oldRegionsByName.get(regionName);
    }

    private List<XpmRegion> loadXpmRegions(Localization localization) {
        StaticContentItem item = null;
        String message = "Could not read XPM regions configuration for pubId: " + localization.getId();
        try {
            item = contentProvider.getStaticContent(REGIONS_PATH, localization.getId(), localization.getPath());
        } catch (ContentProviderException e) {
            LOG.error(message, e);
            throw new DxaRuntimeException(message, e);
        }
        try (final InputStream in = item.getContent();) {
            message += " and path: " + localization.getPath() + " as " + REGIONS_PATH;
            SimpleModule module = new SimpleModule("ComponentTypeMapper", Version.unknownVersion());
            module.addAbstractTypeMapping(ComponentType.class, ComponentTypeImpl.class);
            module.addAbstractTypeMapping(OccurrenceConstraint.class, OccurrenceConstraintImpl.class);
            objectMapper.registerModule(module); // important, otherwise won't have any effect on mapper's configuration
            return objectMapper.readValue(in, new TypeReference<List<XpmRegion>>() {});
        } catch (IOException e) {
            LOG.error(message, e);
            throw new DxaRuntimeException(message, e);
        }
    }
}
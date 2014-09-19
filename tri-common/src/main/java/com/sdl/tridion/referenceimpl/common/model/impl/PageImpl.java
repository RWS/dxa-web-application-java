package com.sdl.tridion.referenceimpl.common.model.impl;

import com.google.common.collect.ImmutableMap;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;

import java.util.LinkedHashMap;
import java.util.Map;

public class PageImpl implements Page {

    private final String id;
    private final String viewName;
    private final Map<String, Region> regions;

    private Map<String, Entity> entities;

    public PageImpl(String id, String viewName, Iterable<? extends Region> regions) {
        this.id = id;
        this.viewName = viewName;

        ImmutableMap.Builder<String, Region> regionsBuilder = ImmutableMap.builder();
        for (Region region : regions) {
            regionsBuilder.put(region.getViewName(), region);
        }
        this.regions = regionsBuilder.build();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    @Override
    public Map<String, Region> getRegions() {
        return regions;
    }

    @Override
    public Region getRegion(String regionViewName) {
        return getRegions().get(regionViewName);
    }

    @Override
    public synchronized Map<String, Entity> getEntities() {
        if (entities == null) {
            // Note that Guava's ImmutableMap.Builder.put() throws an exception if you try to put in duplicate keys;
            // we avoid that by first putting the entities in a standard LinkedHashMap.
            Map<String, Entity> map = new LinkedHashMap<>();
            for (Region region : getRegions().values()) {
                for (Entity entity : region.getEntities()) {
                    map.put(entity.getId(), entity);
                }
            }

            entities = ImmutableMap.copyOf(map);
        }

        return entities;
    }

    @Override
    public Entity getEntity(String entityId) {
        return getEntities().get(entityId);
    }

    @Override
    public String toString() {
        return String.format("PageImpl { id=%s, viewName=%s, regions=%s }", id, viewName, regions.values());
    }
}

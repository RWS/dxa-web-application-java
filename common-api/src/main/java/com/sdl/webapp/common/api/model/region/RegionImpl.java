package com.sdl.webapp.common.api.model.region;

import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.Region;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of {@code Region}.
 */
public class RegionImpl implements Region {

    private String name;
    private Map<String, Entity> entities = new LinkedHashMap<>();
    private Map<String, String> regionData = new HashMap<>();
    private MvcData mvcData;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, Entity> entities) {
        this.entities = entities;
    }

    public void addEntity(Entity entity) {
        this.entities.put(entity.getId(), entity);
    }

    @Override
    public Entity getEntity(String entityId) {
        return this.entities.get(entityId);
    }

    @Override
    public Map<String, String> getRegionData() {
        return regionData;
    }

    public void setRegionData(Map<String, String> regionData) {
        this.regionData = regionData;
    }

    @Override
    public MvcData getMvcData() {
        return mvcData;
    }

    public void setMvcData(MvcData mvcData) {
        this.mvcData = mvcData;
    }

    @Override
    public String toString() {
        return "RegionImpl{" +
                "name='" + name + '\'' +
                ", entities=" + entities +
                ", mvcData='" + mvcData + '\'' +
                '}';
    }
}

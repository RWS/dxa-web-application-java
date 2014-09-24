package com.sdl.tridion.referenceimpl.common.model.region;

import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionImpl implements Region {

    private String name;
    private String module;
    private List<Entity> entities = new ArrayList<>();
    private Map<String, String> regionData = new HashMap<>();
    private String viewName;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public Map<String, String> getRegionData() {
        return regionData;
    }

    public void setRegionData(Map<String, String> regionData) {
        this.regionData = regionData;
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public String toString() {
        return "RegionImpl{" +
                "name='" + name + '\'' +
                ", module='" + module + '\'' +
                ", entities=" + entities +
                ", viewName='" + viewName + '\'' +
                '}';
    }
}

package com.sdl.tridion.referenceimpl.common.model.page;

import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;

import java.util.HashMap;
import java.util.Map;

public class PageImpl implements Page {

    private String id;
    private Map<String, Region> regions = new HashMap<>();
    private Map<String, Entity> entities = new HashMap<>();
    private Map<String, String> pageData = new HashMap<>();
    private String viewName;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Map<String, Region> getRegions() {
        return regions;
    }

    public void setRegions(Map<String, Region> regions) {
        this.regions = regions;
    }

    @Override
    public Map<String, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, Entity> entities) {
        this.entities = entities;
    }

    @Override
    public Map<String, String> getPageData() {
        return pageData;
    }

    public void setPageData(Map<String, String> pageData) {
        this.pageData = pageData;
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
        return "PageImpl{" +
                "id='" + id + '\'' +
                ", regions=" + regions.keySet() +
                ", entities=" + entities.values() +
                ", viewName='" + viewName + '\'' +
                '}';
    }
}

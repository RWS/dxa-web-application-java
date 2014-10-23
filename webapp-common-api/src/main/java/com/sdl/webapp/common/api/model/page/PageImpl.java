package com.sdl.webapp.common.api.model.page;

import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;

import java.util.HashMap;
import java.util.Map;

public class PageImpl implements Page {

    private String id;
    private String title;
    private Map<String, Page> includes = new HashMap<>();
    private Map<String, Region> regions = new HashMap<>();
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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Map<String, Page> getIncludes() {
        return includes;
    }

    public void setIncludes(Map<String, Page> includes) {
        this.includes = includes;
    }

    @Override
    public Map<String, Region> getRegions() {
        return regions;
    }

    public void setRegions(Map<String, Region> regions) {
        this.regions = regions;
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
                ", viewName='" + viewName + '\'' +
                '}';
    }
}

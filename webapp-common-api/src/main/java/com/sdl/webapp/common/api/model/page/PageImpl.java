package com.sdl.webapp.common.api.model.page;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@code Page}.
 */
public class PageImpl implements Page {

    private String id;
    private String name;
    private String title;
    private Map<String, String> meta = new HashMap<>();
    private Map<String, Page> includes = new HashMap<>();
    private Map<String, Region> regions = new HashMap<>();
    private Map<String, String> pageData = new HashMap<>();
    private MvcData mvcData;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
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
    public MvcData getMvcData() {
        return mvcData;
    }

    public void setMvcData(MvcData mvcData) {
        this.mvcData = mvcData;
    }

    @Override
    public String toString() {
        return "PageImpl{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", mvcData='" + mvcData + '\'' +
                '}';
    }
}

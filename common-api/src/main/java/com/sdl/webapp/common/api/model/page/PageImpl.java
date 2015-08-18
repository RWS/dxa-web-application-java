package com.sdl.webapp.common.api.model.page;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@code Page}.
 */
public class PageImpl implements PageModel {

    private String id;
    private String name;
    private String title;
    private Map<String, String> meta = new HashMap<>();
    private Map<String, PageModel> includes = new HashMap<>();
    private RegionModelSet regions = new RegionModelSetImpl();
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
    public Map<String, PageModel> getIncludes() {
        return includes;
    }

    public void setIncludes(Map<String, PageModel> includes) {
        this.includes = includes;
    }

    @Override
    public RegionModelSet getRegions() {
        return regions;
    }

    public void setRegions(RegionModelSet regions) {
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

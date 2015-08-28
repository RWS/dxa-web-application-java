package com.sdl.webapp.common.api.model.region;

import com.sdl.webapp.common.api.model.MvcData;

import java.util.Collections;
import java.util.Map;

/**
 * SimpleRegionMvcData
 *
 * @author nic
 */
public class SimpleRegionMvcData implements MvcData {

    private String controllerAreaName = "Core";
    private String controllerName = "Region";
    private String actionName = "Region";
    private String areaName = "Core";
    private String regionName;

    private Map<String, Object> metadata = Collections.emptyMap();
    private Map<String, String> routeValues = Collections.emptyMap();

    public SimpleRegionMvcData(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public String getControllerAreaName() {
        return controllerAreaName;
    }

    @Override
    public String getControllerName() {
        return controllerName;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public String getAreaName() {
        return areaName;
    }

    @Override
    public String getViewName() {
        return this.regionName;
    }

    @Override
    public String getRegionAreaName() {
        return null;
    }

    @Override
    public String getRegionName() {
        return this.regionName;
    }

    @Override
    public Map<String, String> getRouteValues() {
        return this.routeValues;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }
}

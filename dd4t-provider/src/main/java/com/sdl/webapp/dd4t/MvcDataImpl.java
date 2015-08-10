package com.sdl.webapp.dd4t;

import com.sdl.webapp.common.api.model.MvcData;

import java.util.HashMap;
import java.util.Map;

final class MvcDataImpl implements MvcData {

    private String controllerAreaName;
    private String controllerName;
    private String actionName;

    private String areaName;
    private String viewName;

    private String regionAreaName;
    private String regionName;

    private Map<String, String> routeValues = new HashMap<>();
    private Map<String,Object> metadata = new HashMap<>();

    @Override
    public String getControllerAreaName() {
        return controllerAreaName;
    }

    public void setControllerAreaName(String controllerAreaName) {
        this.controllerAreaName = controllerAreaName;
    }

    @Override
    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public String getRegionAreaName() {
        return regionAreaName;
    }

    public void setRegionAreaName(String regionAreaName) {
        this.regionAreaName = regionAreaName;
    }

    @Override
    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public Map<String, String> getRouteValues() {
        return routeValues;
    }

    public void setRouteValues(Map<String, String> routeValues) {
        this.routeValues = routeValues;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "MvcDataImpl{" +
                "controllerAreaName='" + controllerAreaName + '\'' +
                ", controllerName='" + controllerName + '\'' +
                ", actionName='" + actionName + '\'' +
                ", areaName='" + areaName + '\'' +
                ", viewName='" + viewName + '\'' +
                ", regionAreaName='" + regionAreaName + '\'' +
                ", regionName='" + regionName + '\'' +
                ", routeValues=" + routeValues +
                ", metadata=" + metadata +
                '}';
    }
}

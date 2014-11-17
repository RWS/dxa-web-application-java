package com.sdl.webapp.dd4t;

import com.sdl.webapp.common.api.model.MvcData;

import java.util.Map;

public class DD4TMvcData implements MvcData {

    private String controllerName;
    private String controllerAreaName;
    private String actionName;
    private String viewName;
    private String areaName;
    private String regionName;
    private String regionAreaName;
    private Map<String, String> routeValues;

    @Override
    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    @Override
    public String getControllerAreaName() {
        return controllerAreaName;
    }

    public void setControllerAreaName(String controllerAreaName) {
        this.controllerAreaName = controllerAreaName;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public String getRegionAreaName() {
        return regionAreaName;
    }

    public void setRegionAreaName(String regionAreaName) {
        this.regionAreaName = regionAreaName;
    }

    @Override
    public Map<String, String> getRouteValues() {
        return routeValues;
    }

    public void setRouteValues(Map<String, String> routeValues) {
        this.routeValues = routeValues;
    }

    @Override
    public String toString() {
        return "DD4TMvcData{" +
                "controllerName='" + controllerName + '\'' +
                ", controllerAreaName='" + controllerAreaName + '\'' +
                ", actionName='" + actionName + '\'' +
                ", viewName='" + viewName + '\'' +
                ", areaName='" + areaName + '\'' +
                ", regionName='" + regionName + '\'' +
                ", regionAreaName='" + regionAreaName + '\'' +
                ", routeValues=" + routeValues +
                '}';
    }
}

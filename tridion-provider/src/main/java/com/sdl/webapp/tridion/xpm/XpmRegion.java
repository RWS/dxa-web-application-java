package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class XpmRegion {

    @JsonProperty(value = "Region", required = true)
    private String regionName;

    @JsonProperty(value = "ComponentTypes", required = true)
    private List<ComponentType> componentTypes;

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<ComponentType> getComponentTypes() {
        return componentTypes;
    }

    public void setComponentTypes(List<ComponentType> componentTypes) {
        this.componentTypes = componentTypes;
    }
}

package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sdl.webapp.common.api.xpm.ComponentType;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XpmRegionImpl implements XpmRegion {

    @JsonProperty(value = "Region", required = true)
    private String regionName;

    @JsonProperty(value = "ComponentTypes", required = true)
    private List<ComponentType> componentTypes;

    @Override
    public String getRegionName() {
        return regionName;
    }

    @Override
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public List<ComponentType> getComponentTypes() {
        return componentTypes;
    }

    @Override
    public void setComponentTypes(List<ComponentType> componentTypes) {
        this.componentTypes = componentTypes;
    }
}

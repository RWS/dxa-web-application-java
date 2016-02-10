package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.xpm.ComponentType;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
/**
 * <p>XpmRegionImpl class.</p>
 */
public class XpmRegionImpl implements XpmRegion {

    @JsonProperty(value = "Region", required = true)
    private String regionName;

    @JsonProperty(value = "ComponentTypes", required = true)
    private List<ComponentType> componentTypes;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRegionName() {
        return regionName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /** {@inheritDoc} */
    @Override
    public List<ComponentType> getComponentTypes() {
        return componentTypes;
    }

    /** {@inheritDoc} */
    @Override
    public void setComponentTypes(List<ComponentType> componentTypes) {
        this.componentTypes = componentTypes;
    }
}

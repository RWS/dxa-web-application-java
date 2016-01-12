package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Accessors(chain = true)
public class MvcDataImpl implements MvcData {
    @JsonProperty("ControllerAreaName")
    private String controllerAreaName = "Core";

    @JsonProperty("ControllerName")
    private String controllerName;

    @JsonProperty("ActionName")
    private String actionName;

    @JsonProperty("AreaName")
    private String areaName = "Core";

    @JsonProperty("ViewName")
    private String viewName;

    @JsonProperty("RegionAreaName")
    private String regionAreaName;

    @JsonProperty("RegionName")
    private String regionName;

    @JsonIgnore
    private Map<String, String> routeValues = new HashMap<>();

    @JsonIgnore
    private Map<String, Object> metadata = new HashMap<>();

    public MvcDataImpl() {
    }

    public MvcDataImpl(MvcData mvcData) {
        this.controllerAreaName = mvcData.getControllerAreaName();
        this.controllerName = mvcData.getControllerName();
        this.actionName = mvcData.getActionName();
        this.areaName = mvcData.getAreaName();
        this.viewName = mvcData.getViewName();
        this.regionAreaName = mvcData.getRegionAreaName();
        this.regionName = mvcData.getRegionName();
        this.routeValues = new HashMap<>(mvcData.getRouteValues());
        this.metadata = new HashMap<>(mvcData.getMetadata());
    }

    /**
     * @param qualifiedViewName fully qualified name if defined format. Format must be 'ViewName'
     *                          or 'AreaName:ViewName' or 'AreaName:ControllerName:ViewName.'
     */
    public MvcDataImpl(String qualifiedViewName) {
        String[] parts = qualifiedViewName == null || qualifiedViewName.isEmpty() ? null : qualifiedViewName.split(":");

        if (parts == null || parts.length < 1 || parts.length > 3) {
            throw new IllegalArgumentException(
                    String.format("Invalid format for Qualified View Name: '%s'. " +
                            "Format must be 'ViewName' or 'AreaName:ViewName' " +
                            "or 'AreaName:ControllerName:ViewName.'", qualifiedViewName));
        }

        switch (parts.length) {
            case 1:
                this.setMvcData(parts[0]);
                break;
            case 2:
                this.setMvcData(parts[0], parts[1]);
                break;
            case 3:
                this.setMvcData(parts[0], parts[1], parts[2]);
        }
    }

    @JsonIgnore
    private MvcDataImpl setMvcData(String viewName) {
        this.viewName = viewName;
        return this;
    }

    @JsonIgnore
    private MvcDataImpl setMvcData(String areaName, String viewName) {
        this.setMvcData(viewName);
        this.areaName = areaName;
        return this;
    }

    @JsonIgnore
    private MvcDataImpl setMvcData(String areaName, String controllerName, String viewName) {
        this.setMvcData(areaName, viewName);
        this.controllerName = controllerName;
        return this;
    }

    @Override
    public void mergeIn(MvcData mvcData) {
        controllerName = mergeChoose(controllerName, mvcData.getControllerName());
        areaName = mergeChoose(areaName, mvcData.getAreaName());
        viewName = mergeChoose(viewName, mvcData.getViewName());
    }

    public MvcData defaults(Defaults defaults) {
        return defaults.set(this);
    }

    private String mergeChoose(String oldValue, String newValue) {
        return newValue == null ? oldValue : newValue;
    }

    public enum Defaults {

        CORE_ENTITY("Core", "Entity", "Entity", "Core"),

        CORE_REGION("Core", "Region", "Region", "Core");

        private String controllerAreaName;
        private String controllerName;
        private String actionName;
        private String areaName;

        Defaults(String controllerAreaName, String controllerName, String actionName, String areaName) {
            this.controllerAreaName = controllerAreaName;
            this.controllerName = controllerName;
            this.actionName = actionName;
            this.areaName = areaName;
        }

        private MvcData set(MvcDataImpl mvcData) {
            mvcData.controllerAreaName = mvcData.mergeChoose(this.controllerAreaName, mvcData.controllerAreaName);
            mvcData.controllerName = mvcData.mergeChoose(this.controllerName, mvcData.controllerName);
            mvcData.actionName = mvcData.mergeChoose(this.actionName, mvcData.actionName);
            mvcData.areaName = mvcData.mergeChoose(this.areaName, mvcData.areaName);
            return mvcData;
        }
    }
}

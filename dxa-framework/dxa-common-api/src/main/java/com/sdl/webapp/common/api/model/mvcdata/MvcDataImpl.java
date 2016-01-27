package com.sdl.webapp.common.api.model.mvcdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.model.MvcData;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class MvcDataImpl implements MvcData {
    @JsonProperty("ControllerAreaName")
    private String controllerAreaName;

    @JsonProperty("ControllerName")
    private String controllerName;

    @JsonProperty("ActionName")
    private String actionName;

    @JsonProperty("AreaName")
    private String areaName;

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

    @SuppressWarnings("unused")
    /**
     * Default field initialization for Lombok Builder.
     */
    protected MvcDataImpl(String controllerAreaName, String controllerName, String actionName, String areaName,
                          String viewName, String regionAreaName, String regionName,
                          Map<String, String> routeValues, Map<String, Object> metadata) {
        this.controllerName = controllerName;
        this.actionName = actionName;
        this.viewName = viewName;
        this.regionAreaName = regionAreaName;
        this.regionName = regionName;
        this.controllerAreaName = controllerAreaName;
        this.areaName = areaName;
        this.routeValues = routeValues == null ? new HashMap<String, String>() : routeValues;
        this.metadata = metadata == null ? new HashMap<String, Object>() : metadata;
    }

    protected MvcDataImpl() {
    }

    protected MvcDataImpl(MvcData mvcData) {
        this.controllerAreaName = mvcData.getControllerAreaName();
        this.controllerName = mvcData.getControllerName();
        this.actionName = mvcData.getActionName();
        this.areaName = mvcData.getAreaName();
        this.viewName = mvcData.getViewName();
        this.regionAreaName = mvcData.getRegionAreaName();
        this.regionName = mvcData.getRegionName();
        this.routeValues = mvcData.getRouteValues();
        this.metadata = mvcData.getMetadata();
    }
}

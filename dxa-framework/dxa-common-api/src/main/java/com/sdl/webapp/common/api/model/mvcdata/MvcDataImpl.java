package com.sdl.webapp.common.api.model.mvcdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.model.MvcData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Map<String, String> routeValues;

    @JsonIgnore
    private Map<String, Object> metadata;

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

    @Override
    public String getControllerAreaName() {
        return controllerAreaName == null ? "Core" : controllerAreaName;
    }

    @Override
    public String getAreaName() {
        return areaName == null ? "Core" : areaName;
    }
}

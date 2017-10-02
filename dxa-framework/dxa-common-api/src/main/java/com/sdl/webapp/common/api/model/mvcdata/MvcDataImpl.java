package com.sdl.webapp.common.api.model.mvcdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.webapp.common.controller.ControllerUtils.FRAMEWORK_CONTROLLER_MAPPING;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter(AccessLevel.PROTECTED)
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(exclude = {"regionAreaName", "regionName"})
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MvcDataImpl implements MvcData {

    protected static final List<String> INCLUDE_CONTROLLERS = Splitter.on(",").trimResults().splitToList("Entity, List, Navigation, Region");

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

    @JsonIgnore
    private String regionAreaName;

    @JsonIgnore
    private String regionName;

    @JsonIgnore
    private Map<String, String> routeValues = new HashMap<>();

    @JsonIgnore
    private Map<String, Object> metadata = new HashMap<>();

    public MvcDataImpl(@NotNull MvcData other) {
        this.controllerAreaName = other.getControllerAreaName();
        this.controllerName = other.getControllerName();
        this.actionName = other.getActionName();
        this.areaName = other.getAreaName();
        this.viewName = other.getViewName();
        this.regionAreaName = other.getRegionAreaName();
        this.regionName = other.getRegionName();
        if (other.getRouteValues() != null) {
            this.routeValues.putAll(other.getRouteValues());
        }
        if (other.getMetadata() != null) {
            this.metadata.putAll(other.getRouteValues());
        }
    }

    public static MvcDataImplBuilder newBuilder() {
        return new MvcDataImplBuilder();
    }

    public Object getMetadataValue(String key) {
        return this.metadata.get(key);
    }

    public MvcDataImplBuilder toBuilder() {
        return MvcDataImplBuilder.toBuilder(this);
    }

    @Override
    public String getControllerAreaName() {
        return INCLUDE_CONTROLLERS.contains(getControllerName()) ? FRAMEWORK_CONTROLLER_MAPPING : controllerAreaName;
    }

    @Override
    public MvcData deepCopy() {
        return new MvcDataImpl(this);
    }

    public void addMetadataValue(String key, Object obj) {
        this.metadata.put(key, obj);
    }

    /**
     * Builder for MvcData. Lombok's implementation fails on Javadoc.
     */
    public static class MvcDataImplBuilder {

        private String controllerAreaName;

        private String controllerName;

        private String actionName;

        private String areaName;

        private String viewName;

        private String regionAreaName;

        private String regionName;

        private Map<String, String> routeValues = new HashMap<>();

        private Map<String, Object> metadata = new HashMap<>();

        protected static MvcDataImplBuilder toBuilder(MvcData mvcData) {
            return (new MvcDataImplBuilder())
                    .controllerAreaName(mvcData.getControllerAreaName())
                    .controllerName(mvcData.getControllerName())
                    .actionName(mvcData.getActionName())
                    .areaName(mvcData.getAreaName())
                    .viewName(mvcData.getViewName())
                    .regionAreaName(mvcData.getRegionAreaName())
                    .regionName(mvcData.getRegionName())
                    .routeValues(mvcData.getRouteValues())
                    .metadata(mvcData.getMetadata());
        }

        public MvcDataImplBuilder controllerAreaName(String controllerAreaName) {
            this.controllerAreaName = controllerAreaName;
            return this;
        }

        public MvcDataImplBuilder controllerName(String controllerName) {
            this.controllerName = controllerName;
            return this;
        }

        public MvcDataImplBuilder actionName(String actionName) {
            this.actionName = actionName;
            return this;
        }

        public MvcDataImplBuilder areaName(String areaName) {
            this.areaName = areaName;
            return this;
        }

        public MvcDataImplBuilder viewName(String viewName) {
            this.viewName = StringUtils.dashify(viewName);
            log.debug("Replaced spaces with dashes for a view name {} --> {}", viewName, this.viewName);
            return this;
        }

        public MvcDataImplBuilder regionAreaName(String regionAreaName) {
            this.regionAreaName = regionAreaName;
            return this;
        }

        public MvcDataImplBuilder regionName(String regionName) {
            this.regionName = regionName;
            return this;
        }

        public MvcDataImplBuilder routeValues(Map<String, String> routeValues) {
            this.routeValues = routeValues == null ? Collections.emptyMap() : routeValues;
            return this;
        }

        public MvcDataImplBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public MvcDataImpl build() {
            return new MvcDataImpl(controllerAreaName, controllerName, actionName, areaName, viewName, regionAreaName, regionName, routeValues, metadata);
        }
    }
}

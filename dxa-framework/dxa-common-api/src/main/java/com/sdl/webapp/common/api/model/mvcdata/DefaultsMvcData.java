package com.sdl.webapp.common.api.model.mvcdata;

import lombok.Getter;

@Getter
public enum DefaultsMvcData {

    CORE_ENTITY("Core", "Entity", "Entity", "Core"),

    CORE_PAGE("Core", "Page", "Page", "Core"),

    CORE_REGION("Core", "Region", "Region", "Core");

    private String controllerAreaName;
    private String controllerName;
    private String actionName;
    private String areaName;

    DefaultsMvcData(String controllerAreaName, String controllerName, String actionName, String areaName) {
        this.controllerAreaName = controllerAreaName;
        this.controllerName = controllerName;
        this.actionName = actionName;
        this.areaName = areaName;
    }
}

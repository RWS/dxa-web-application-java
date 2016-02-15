package com.sdl.webapp.common.api.model.mvcdata;

import lombok.Getter;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.CORE_AREA_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.ENTITY_ACTION_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.ENTITY_CONTROLLER_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.ERROR_ACTION_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.ERROR_CONTROLLER_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.PAGE_ACTION_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.PAGE_CONTROLLER_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.REGION_ACTION_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.REGION_CONTROLLER_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.SHARED_AREA_NAME;

@Getter
/**
 * <p>DefaultsMvcData class.</p>
 */
public enum DefaultsMvcData {

    CORE_ENTITY("Core", CORE_AREA_NAME, ENTITY_CONTROLLER_NAME, ENTITY_ACTION_NAME),

    CORE_PAGE("Core", CORE_AREA_NAME, PAGE_CONTROLLER_NAME, PAGE_ACTION_NAME),

    CORE_REGION("Core", CORE_AREA_NAME, REGION_CONTROLLER_NAME, REGION_ACTION_NAME),

    ERROR_ENTITY("Shared", SHARED_AREA_NAME, ERROR_CONTROLLER_NAME, ERROR_ACTION_NAME);

    private String controllerAreaName;
    private String controllerName;
    private String actionName;
    private String areaName;

    DefaultsMvcData(String controllerAreaName, String areaName, String controllerName, String actionName) {
        this.controllerAreaName = controllerAreaName;
        this.areaName = areaName;
        this.controllerName = controllerName;
        this.actionName = actionName;
    }

    public interface CoreAreaConstants {
        String CORE_AREA_NAME = "Core";

        String SHARED_AREA_NAME = "Shared";

        String PAGE_CONTROLLER_NAME = "Page";
        String PAGE_ACTION_NAME = "Page";

        String REGION_CONTROLLER_NAME = "Region";
        String REGION_ACTION_NAME = "Region";

        String ENTITY_CONTROLLER_NAME = "Entity";
        String ENTITY_ACTION_NAME = "Entity";

        String LIST_CONTROLLER_NAME = "List";
        String LIST_ACTION_NAME = "List";

        String ERROR_CONTROLLER_NAME = "Error";
        String ERROR_ACTION_NAME = "Entity";

        String NAVIGATION_CONTROLLER_NAME = "Navigation";
        String NAVIGATION_ACTION_NAME = "Navigation";
        String SITEMAP_ACTION_NAME = "SiteMap";
    }
}

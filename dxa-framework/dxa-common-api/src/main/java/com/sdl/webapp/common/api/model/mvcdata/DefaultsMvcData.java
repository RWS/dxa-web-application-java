package com.sdl.webapp.common.api.model.mvcdata;

import lombok.Getter;

import static com.sdl.webapp.common.controller.ControllerUtils.FRAMEWORK_CONTROLLER_MAPPING;
import static com.sdl.webapp.common.util.InitializationUtils.getConfiguration;

@Getter
public enum DefaultsMvcData {

    CORE_ENTITY(FRAMEWORK_CONTROLLER_MAPPING, getDefaultAreaName(), "Entity", "Entity"),

    CORE_PAGE(FRAMEWORK_CONTROLLER_MAPPING, getDefaultAreaName(), "Page", "Page"),

    CORE_REGION(FRAMEWORK_CONTROLLER_MAPPING, getDefaultAreaName(), "Region", "Region"),

    ERROR_ENTITY("Shared", "Shared", "Error", "Entity");

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


    /**
     * Returns the pre-configured area name with defaults to {@code Core}.
     *
     * @return pre-configured area name or default name
     */
    public static String getDefaultAreaName() {
        return getConfiguration("dxa.web.default.area.name", "Core");
    }

    /**
     * Returns the pre-configured region name with defaults to {@code Main}.
     *
     * @return pre-configured region name or default name
     */
    public static String getDefaultRegionName() {
        return getConfiguration("dxa.web.default.region.name", "Main");
    }

    /**
     * Returns the pre-configured controller name with defaults to {@code Entity}.
     *
     * @return pre-configured controller name or default name
     */
    public static String getDefaultControllerName() {
        return getConfiguration("dxa.web.default.controller.name", "Entity");
    }

    /**
     * Returns the pre-configured controller area name with defaults to {@code Core}.
     *
     * @return pre-configured controller area name or default name
     */
    public static String getDefaultControllerAreaName() {
        return getConfiguration("dxa.web.default.controller.area.name", "Core");
    }

    /**
     * Returns the pre-configured action name with defaults to {@code Entity}.
     *
     * @return pre-configured action name or default name
     */
    public static String getDefaultActionName() {
        return getConfiguration("dxa.web.default.action.name", "Entity");
    }

}

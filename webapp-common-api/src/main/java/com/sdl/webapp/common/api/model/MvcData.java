package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Data for the MVC framework to determine how a view model object should be handled.
 */
public interface MvcData {

    String getControllerName();

    String getControllerAreaName();

    String getActionName();

    String getViewName();

    String getAreaName();

    String getRegionName();

    String getRegionAreaName();

    Map<String, String> getRouteValues();
}

package com.sdl.webapp.common.api.model;

import java.util.Map;

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

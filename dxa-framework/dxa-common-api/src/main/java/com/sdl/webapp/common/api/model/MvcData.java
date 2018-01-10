package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Data for the MVC framework to determine how a view model object should be handled.
 *
 * @dxa.publicApi
 */
public interface MvcData {

    String getControllerAreaName();

    String getControllerName();

    String getActionName();

    String getAreaName();

    String getViewName();

    String getRegionAreaName();

    String getRegionName();

    Map<String, String> getRouteValues();

    /**
     * Makes a deep copy of the current object.
     * It is important that {@code mvcData.deepCopy != mvcData && mvcData.equals(mvcData.deepCopy())} evaluates to {@code true}
     *
     * @return a deep copy
     */
    MvcData deepCopy();

    Map<String, Object> getMetadata();

    void addMetadataValue(String key, Object obj);
}

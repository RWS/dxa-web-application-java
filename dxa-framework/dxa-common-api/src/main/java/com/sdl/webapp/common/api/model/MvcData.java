package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Data for the MVC framework to determine how a view model object should be handled.
 */
public interface MvcData {

    /**
     * <p>getControllerAreaName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getControllerAreaName();

    /**
     * <p>getControllerName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getControllerName();

    /**
     * <p>getActionName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getActionName();

    /**
     * <p>getAreaName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getAreaName();

    /**
     * <p>getViewName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getViewName();

    /**
     * <p>getRegionAreaName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getRegionAreaName();

    /**
     * <p>getRegionName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getRegionName();

    /**
     * <p>getRouteValues.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, String> getRouteValues();

    /**
     * <p>Returns a map of metadata for current MvcData.</p>
     * todo dxa2 will return a copy
     * @return a {@link java.util.Map} object with metadata
     */
    Map<String, Object> getMetadata();

    /**
     * Adds value to metadata of current MvcData object.
     *
     * @param key ket of metadata value
     * @param obj value to add
     */
    void addMetadataValue(String key, Object obj);
}

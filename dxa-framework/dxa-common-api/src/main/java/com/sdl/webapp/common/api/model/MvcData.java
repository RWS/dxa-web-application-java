package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Data for the MVC framework to determine how a view model object should be handled.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
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
     * <p>getMetadata.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, Object> getMetadata();
}

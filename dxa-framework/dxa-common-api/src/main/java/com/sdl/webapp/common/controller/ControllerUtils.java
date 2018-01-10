package com.sdl.webapp.common.controller;

import com.google.common.base.Joiner;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @dxa.publicApi
 */
public final class ControllerUtils {

    public static final String INCLUDE_PATH_PREFIX = "/system/mvc/";

    public static final String FRAMEWORK_CONTROLLER_MAPPING = "Framework";

    public static final String INCLUDE_MAPPING = INCLUDE_PATH_PREFIX + FRAMEWORK_CONTROLLER_MAPPING;

    public static final String SECTION_ERROR_VIEW = "Shared/Error/SectionError";

    public static final String SERVER_ERROR_VIEW = "Shared/Error/ServerError";

    private ControllerUtils() {
    }

    public static String getIncludePath(RegionModel region) {
        final MvcData mvcData = region.getMvcData();
        return getIncludePathPrefix(mvcData) +
                region.getName() + '/' +
                getQueryParameters(mvcData);
    }

    public static String getIncludePath(EntityModel entity) {
        final MvcData mvcData = entity.getMvcData();
        return getIncludePathPrefix(mvcData) +
                entity.getId() +
                getQueryParameters(mvcData);
    }

    public static String getIncludeErrorPath() {
        return ControllerUtils.INCLUDE_PATH_PREFIX + ControllerUtils.SECTION_ERROR_VIEW;
    }

    private static String getIncludePathPrefix(MvcData mvcData) {
        return INCLUDE_PATH_PREFIX +
                mvcData.getControllerAreaName() + '/' +
                mvcData.getControllerName() + '/' +
                mvcData.getActionName() + '/';
    }

    private static String getQueryParameters(MvcData mvcData) {
        final List<String> queryParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : mvcData.getRouteValues().entrySet()) {
            queryParams.add(entry.getKey() + '=' + entry.getValue());
        }

        return queryParams.isEmpty() ? "" : ('?' + Joiner.on('&').join(queryParams));
    }
}

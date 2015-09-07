package com.sdl.webapp.common.controller;

import com.google.common.base.Joiner;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ControllerUtils {

    public static final String INCLUDE_PATH_PREFIX = "/system/mvc/";

    public static final String SECTION_ERROR_VIEW = "Shared/SectionError";
    public static final String NOT_FOUND_ERROR_VIEW = "Shared/NotFoundError";
    public static final String SERVER_ERROR_VIEW = "Shared/ServerError";

    private ControllerUtils() {
    }

    public static String getIncludePath(PageModel page) {
       /* final MvcData mvcData = page.getMvcData();
        return getIncludePathPrefix(mvcData) +
                page.getName() +
                getQueryParameters(mvcData);*/
    	throw new UnsupportedOperationException("getIncludePath with parameter PageModel is no longer supported"); 
    }

    public static String getIncludePath(RegionModel region) {
        final MvcData mvcData = region.getMvcData();
        return getIncludePathPrefix(mvcData) +
                region.getName() + "/" + 
                getQueryParameters(mvcData);
    }

    public static String getIncludePath(EntityModel entity) {
        final MvcData mvcData = entity.getMvcData();
        return getIncludePathPrefix(mvcData) +
                mvcData.getRegionName() + "/" +
                entity.getId() +
                getQueryParameters(mvcData);
    }

    private static String getIncludePathPrefix(MvcData mvcData) {
        return INCLUDE_PATH_PREFIX +
                mvcData.getControllerAreaName() + "/" +
                mvcData.getControllerName() + "/" +
                mvcData.getActionName() + "/";
    }

    private static String getQueryParameters(MvcData mvcData) {
        final List<String> queryParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : mvcData.getRouteValues().entrySet()) {
            queryParams.add(entry.getKey() + '=' + entry.getValue());
        }

        return queryParams.isEmpty() ? "" : ("?" + Joiner.on('&').join(queryParams));
    }
}

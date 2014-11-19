package com.sdl.webapp.main.controller;

import com.google.common.base.Joiner;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ControllerUtils {

    public static final String REQUEST_PATH_PREFIX = "/system/mvc/";

    private ControllerUtils() {
    }

    public static String getRequestPath(Page page) {
        final MvcData mvcData = page.getMvcData();
        return getRequestPathPrefix(mvcData) +
                page.getName() +
                getQueryParameters(mvcData);
    }

    public static String getRequestPath(Region region) {
        final MvcData mvcData = region.getMvcData();
        return getRequestPathPrefix(mvcData) +
                region.getName() +
                getQueryParameters(mvcData);
    }

    public static String getRequestPath(Entity entity) {
        final MvcData mvcData = entity.getMvcData();
        return getRequestPathPrefix(mvcData) +
                mvcData.getRegionName() + "/" +
                entity.getId() +
                getQueryParameters(mvcData);
    }

    private static String getRequestPathPrefix(MvcData mvcData) {
        return REQUEST_PATH_PREFIX +
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

package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.MvcData;

import javax.servlet.http.HttpServletRequest;

//todo dxa2 remove
/**
 * Resolves a view name based on MVC data and view's type.
 *
 * @deprecated since 1.5, use {@link com.sdl.dxa.mvc.ViewNameResolver}
 */
@Deprecated
public interface ViewResolver {

    /**
     * Resolves a view name from {@link MvcData}, view type and a request.
     *
     * @param mvcData  mvc data with information about the view needed
     * @param viewType type of a view
     * @param request  current request
     * @return view name
     * @deprecated since 1.5, use {@link com.sdl.dxa.mvc.ViewNameResolver#resolveView(MvcData, String)}
     */
    @Deprecated
    String resolveView(MvcData mvcData, String viewType, HttpServletRequest request);

    /**
     * Resolves a view name from {@link MvcData}, view type and a request.
     *
     * @param viewBaseDir abse dir
     * @param viewName    type of a view
     * @param mvcData     mvc data with information about the view needed
     * @param request     current request
     * @return view name
     * @deprecated since 1.5, use {@link com.sdl.dxa.mvc.ViewNameResolver#resolveView(MvcData, String)}
     */
    @Deprecated
    String resolveView(String viewBaseDir, String viewName, MvcData mvcData, HttpServletRequest request);
}

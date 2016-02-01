package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.MvcData;

import javax.servlet.http.HttpServletRequest;

/**
 * DXA View Resolver.
 * <p>
 * Resolves a view based model MVC data and it's view type. Can for example be used to route to contextual views.
 * </p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface ViewResolver {

    /**
     * <p>resolveView.</p>
     *
     * @param mvcData  a {@link com.sdl.webapp.common.api.model.MvcData} object.
     * @param viewType a {@link java.lang.String} object.
     * @param request  a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link java.lang.String} object.
     */
    String resolveView(MvcData mvcData, String viewType, HttpServletRequest request);

    /**
     * <p>resolveView.</p>
     *
     * @param viewBaseDir a {@link java.lang.String} object.
     * @param viewName    a {@link java.lang.String} object.
     * @param mvcData     a {@link com.sdl.webapp.common.api.model.MvcData} object.
     * @param request     a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link java.lang.String} object.
     */
    String resolveView(String viewBaseDir, String viewName, MvcData mvcData, HttpServletRequest request);
}

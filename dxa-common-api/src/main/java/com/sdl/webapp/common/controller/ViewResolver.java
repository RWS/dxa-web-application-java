package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.MvcData;

import javax.servlet.http.HttpServletRequest;

/**
 * DXA View Resolver.
 * <p/>
 * Resolves a view based model MVC data and it's view type. Can for example be used to route to contextual views.
 */
public interface ViewResolver {

    String resolveView(MvcData mvcData, String viewType, HttpServletRequest request);

    String resolveView(String viewBaseDir, String viewName, MvcData mvcData, HttpServletRequest request);
}

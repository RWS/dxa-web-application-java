package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.controller.ViewResolver;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class DefaultViewResolver implements ViewResolver {

    @Override
    public String resolveView(MvcData mvcData, String viewType, HttpServletRequest request) {
        return mvcData.getAreaName() + "/" + viewType + "/" + mvcData.getViewName();
    }

    @Override
    public String resolveView(String viewBaseDir, String viewName, MvcData mvcData, HttpServletRequest request) {
        return viewBaseDir + "/" + viewName;
    }
}

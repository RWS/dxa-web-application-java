package com.sdl.webapp.common.impl;

import com.sdl.dxa.mvc.ViewNameResolver;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.controller.ViewResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class DefaultViewResolver implements ViewNameResolver, ViewResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveView(MvcData mvcData, String viewType, HttpServletRequest request) {
        return mvcData.getAreaName() + '/' + viewType + '/' + mvcData.getViewName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveView(String viewBaseDir, String viewName, MvcData mvcData, HttpServletRequest request) {
        return viewBaseDir + '/' + viewName;
    }

    @Override
    public String resolveView(MvcData mvcData, String viewType) {
        return mvcData.getAreaName() + '/' + viewType + '/' + mvcData.getViewName();
    }
}

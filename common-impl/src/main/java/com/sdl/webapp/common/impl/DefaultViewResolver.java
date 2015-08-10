package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.DefaultImplementation;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.controller.ViewResolver;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * DefaultViewResolver
 *
 * @author nic
 */
@Component
public class DefaultViewResolver extends DefaultImplementation<ViewResolver> implements ViewResolver {

    @Override
    public Class<?> getObjectType() {
        return ViewResolver.class;
    }

    @Override
    public String resolveView(MvcData mvcData, String viewType, HttpServletRequest request) {
        return mvcData.getAreaName() + "/" + viewType + "/" + mvcData.getViewName();
    }

    @Override
    public String resolveView(String viewBaseDir, String viewName, MvcData mvcData, HttpServletRequest request) {
        return viewBaseDir + "/" + viewName;
    }
}

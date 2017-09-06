package com.sdl.webapp.common.impl;

import com.sdl.dxa.mvc.ViewNameResolver;
import com.sdl.webapp.common.api.model.MvcData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultViewResolver implements ViewNameResolver {

    @Override
    public String resolveView(MvcData mvcData, String viewType) {
        return mvcData.getAreaName() + '/' + viewType + '/' + mvcData.getViewName();
    }
}

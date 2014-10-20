package com.sdl.webapp.main.interceptor;

import com.sdl.webapp.common.api.Localization;
import com.sdl.webapp.common.api.LocalizationResolver;
import com.sdl.webapp.main.RequestAttributeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LocalizationResolverInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LocalizationResolver localizationResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // NOTE: The full URL (including protocol, domain, port etc.) must be passed here
        final String url = request.getRequestURL().toString();
        final Localization localization = localizationResolver.getLocalization(url);
        request.setAttribute(RequestAttributeNames.LOCALIZATION, localization);
        return true;
    }
}

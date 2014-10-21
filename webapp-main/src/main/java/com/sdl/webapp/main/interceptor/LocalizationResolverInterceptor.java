package com.sdl.webapp.main.interceptor;

import com.sdl.webapp.common.api.LocalizationResolver;
import com.sdl.webapp.common.api.WebRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LocalizationResolverInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(LocalizationResolverInterceptor.class);

    @Autowired
    private LocalizationResolver localizationResolver;

    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // NOTE: The full URL (including protocol, domain, port etc.) must be passed here
        final String url = request.getRequestURL().toString();
        LOG.debug("preHandle: {}", url);

        webRequestContext.setLocalization(localizationResolver.getLocalization(url));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOG.debug("afterCompletion: {}", request.getRequestURL().toString());
    }
}

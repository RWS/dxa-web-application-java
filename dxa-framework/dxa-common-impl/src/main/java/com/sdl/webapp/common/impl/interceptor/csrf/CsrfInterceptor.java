package com.sdl.webapp.common.impl.interceptor.csrf;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CsrfInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean success = CsrfUtils.verifyToken(request);
        if (!success) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        return success;
    }
}

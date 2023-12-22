package com.sdl.webapp.common.impl.interceptor;

import com.sdl.webapp.common.api.ThreadLocalManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>ThreadLocalInterceptor class.</p>
 */
public class ThreadLocalInterceptor implements HandlerInterceptor {

    @Autowired
    private ThreadLocalManager threadLocalManager;

    /**
     * {@inheritDoc}
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (request.getDispatcherType() != DispatcherType.INCLUDE) {
            this.threadLocalManager.clearAll();
        }
    }
}



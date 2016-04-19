package com.sdl.webapp.common.impl.util.spring;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletResponseInScopeFilter implements Filter {

    private ThreadLocal<HttpServletResponse> servletResponseThreadLocal = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        servletResponseThreadLocal.set((HttpServletResponse) response);
        // original response
        chain.doFilter(request, response);
        servletResponseThreadLocal.remove();
    }

    @Override
    public void destroy() {
    }

    /**
     * Only to be used by the BeanFactory.
     */
    HttpServletResponse getHttpServletResponse() {
        return servletResponseThreadLocal.get();
    }
}

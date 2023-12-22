package com.sdl.webapp.common.impl.interceptor;

import org.apache.http.HttpStatus;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Health check filter that goes before {@link com.sdl.webapp.common.api.WebRequestContext}, and thus does not fail
 * when localization is not resolved. Always returns {@code 200 HTTP} and stops chaining.
 */
public class HealthCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing here
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        ((HttpServletResponse) servletResponse).setStatus(HttpStatus.SC_OK);
    }

    @Override
    public void destroy() {
        // nothing here
    }
}

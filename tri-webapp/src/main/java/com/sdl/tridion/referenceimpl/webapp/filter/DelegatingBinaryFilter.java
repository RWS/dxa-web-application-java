package com.sdl.tridion.referenceimpl.webapp.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;
import java.util.Map;

public class DelegatingBinaryFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(DelegatingBinaryFilter.class);

    private static final String DD4T_BINARY_FILTER = "dd4tBinaryFilter";

    private Filter binaryFilter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                filterConfig.getServletContext());

        binaryFilter = springContext.getBeansOfType(Filter.class).get(DD4T_BINARY_FILTER);
        LOG.debug("binaryFilter: {}", binaryFilter);

        if (binaryFilter != null) {
            binaryFilter.init(filterConfig);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (binaryFilter != null) {
            binaryFilter.doFilter(request, response, chain);
        }
    }

    @Override
    public void destroy() {
        if (binaryFilter != null) {
            binaryFilter.destroy();
        }
    }
}

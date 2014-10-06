package com.sdl.tridion.referenceimpl.webapp.filter;

import com.sdl.tridion.referenceimpl.common.AmbientDataSpringWrapper;
import com.tridion.ambientdata.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * Servlet filter that makes the Ambient Data claim store available in the Spring application context.
 */
public class AmbientDataSpringFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(AmbientDataSpringFilter.class);

    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
                .getBean(AmbientDataSpringWrapper.class)
                .setClaimStore(WebContext.getCurrentClaimStore());

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

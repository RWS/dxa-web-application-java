package com.sdl.tridion.referenceimpl.webapp.filter;

import com.sdl.tridion.referenceimpl.common.config.Localization;
import com.sdl.tridion.referenceimpl.common.config.LocalizationProvider;
import com.sdl.tridion.referenceimpl.common.config.WebAppContext;
import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * Servlet filter that determines the localization for the request and stores it in the {@code WebRequestContext}.
 */
public class LocalizationFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(LocalizationFilter.class);

    private LocalizationProvider localizationProvider;
    private WebRequestContext webRequestContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        final ServletContext servletContext = filterConfig.getServletContext();
        final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                servletContext);

        localizationProvider = springContext.getBean(LocalizationProvider.class);
        webRequestContext = springContext.getBean(WebRequestContext.class);

        final File webAppRootPath = new File(servletContext.getRealPath("/"));
        springContext.getBean(WebAppContext.class).setWebAppRootPath(webAppRootPath);
        LOG.debug("webAppRootPath: {}", webAppRootPath.getPath());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Note: The full URL (including protocol, domain, port etc.) is necessary here
        String url = ((HttpServletRequest) request).getRequestURL().toString();

        final Localization localization = localizationProvider.getLocalization(url);
        if (localization != null) {
            webRequestContext.setLocalization(localization);
        } else {
            LOG.warn("Could not determine localization for url: {}", url);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

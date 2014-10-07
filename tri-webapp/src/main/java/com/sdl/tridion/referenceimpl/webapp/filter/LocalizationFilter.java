package com.sdl.tridion.referenceimpl.webapp.filter;

import com.sdl.tridion.referenceimpl.common.config.Localization;
import com.sdl.tridion.referenceimpl.common.config.TridionConfiguration;
import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Servlet filter that determines the localization for the request and stores it in the {@code WebRequestContext}.
 */
public class LocalizationFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(Localization.class);

    private TridionConfiguration tridionConfiguration;

    private WebRequestContext webRequestContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                filterConfig.getServletContext());

        tridionConfiguration = springContext.getBean(TridionConfiguration.class);

        webRequestContext = springContext.getBean(WebRequestContext.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Note: The full URL (including protocol, domain, port etc.) is necessary here
        String url = ((HttpServletRequest) request).getRequestURL().toString();

        final Localization localization = tridionConfiguration.getLocalization(url);
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

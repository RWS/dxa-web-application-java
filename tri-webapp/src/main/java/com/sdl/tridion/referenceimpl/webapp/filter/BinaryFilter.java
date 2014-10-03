package com.sdl.tridion.referenceimpl.webapp.filter;

import com.sdl.tridion.referenceimpl.common.StaticFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BinaryFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryFilter.class);

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    private ServletContext servletContext;

    private StaticFileManager staticFileManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();

        staticFileManager = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
                .getBean(StaticFileManager.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final String url = URL_PATH_HELPER.getRequestUri(req).replace(URL_PATH_HELPER.getContextPath(req), "");

        if (!handleRequest(url, response)) {
            chain.doFilter(request, response);
        }
    }

    private boolean handleRequest(String url, ServletResponse response) throws IOException, ServletException {
        LOG.debug("handleRequest: {}", url);

        final File file = new File(servletContext.getRealPath(url));
        if (staticFileManager.getStaticContent(url, file)) {
            LOG.debug("Sending response with content of file: {}", file);
            Files.copy(file.toPath(), response.getOutputStream());
            return true;
        } else {
            LOG.debug("No static content found for: {}", url);
            return false;
        }
    }

    @Override
    public void destroy() {
    }
}

package com.sdl.tridion.referenceimpl.webapp.filter;

import com.sdl.tridion.referenceimpl.common.StaticFileManager;
import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BinaryFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryFilter.class);

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    private ServletContext servletContext;

    private WebRequestContext webRequestContext;
    private StaticFileManager staticFileManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();

        final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        webRequestContext = springContext.getBean(WebRequestContext.class);
        staticFileManager = springContext.getBean(StaticFileManager.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final String url = URL_PATH_HELPER.getRequestUri(req).replace(URL_PATH_HELPER.getContextPath(req), "");

        if (webRequestContext.getLocalization().isMediaUrl(url)) {
            handleRequest(url, new ServletServerHttpRequest((HttpServletRequest) request),
                    new ServletServerHttpResponse((HttpServletResponse) response));
        } else {
            chain.doFilter(request, response);
        }
    }

    private void handleRequest(String url, ServletServerHttpRequest request, ServletServerHttpResponse response) throws IOException, ServletException {
        LOG.debug("handleRequest: {}", url);

        final File file = new File(servletContext.getRealPath(url));

        HttpStatus responseCode;
        if (staticFileManager.getStaticContent(url, file)) {
            // NOTE: Unfortunately, in this version of Spring the method getIfNotModifiedSince() has the wrong name
            long ifModifiedSince = request.getHeaders().getIfNotModifiedSince();
            if (ifModifiedSince > 0L && file.lastModified() - ifModifiedSince < 1000L) {
                responseCode = HttpStatus.NOT_MODIFIED;
            } else {
                responseCode = HttpStatus.OK;
                Files.copy(file.toPath(), response.getBody());
            }
        } else {
            LOG.debug("No static content found for: {}", url);
            responseCode = HttpStatus.NOT_FOUND;
        }

        LOG.debug("Response code: {} for url: {}", responseCode, url);
        response.setStatusCode(responseCode);
        response.close();
    }

    @Override
    public void destroy() {
    }
}

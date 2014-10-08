package com.sdl.tridion.referenceimpl.webapp.filter;

import com.sdl.tridion.referenceimpl.common.StaticContentProvider;
import com.sdl.tridion.referenceimpl.common.config.Localization;
import com.sdl.tridion.referenceimpl.common.config.WebAppContext;
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

public class StaticContentFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(StaticContentFilter.class);

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    private WebAppContext webAppContext;
    private WebRequestContext webRequestContext;
    private StaticContentProvider staticContentProvider;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                filterConfig.getServletContext());

        webAppContext = springContext.getBean(WebAppContext.class);
        webRequestContext = springContext.getBean(WebRequestContext.class);
        staticContentProvider = springContext.getBean(StaticContentProvider.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final String url = URL_PATH_HELPER.getRequestUri(request).replace(URL_PATH_HELPER.getContextPath(request), "");
        final Localization localization = webRequestContext.getLocalization();
        if (localization.isStaticResourceUrl(url)) {
            handleRequest(url, localization, new ServletServerHttpRequest(request), new ServletServerHttpResponse(response));
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    private void handleRequest(String url, Localization localization, ServletServerHttpRequest request, ServletServerHttpResponse response)
            throws IOException, ServletException {
        final File file = new File(new File(webAppContext.getStaticsPath(), localization.getPath()), url);
        LOG.debug("handleRequest: {}, file: {}", url, file);

        final HttpStatus responseCode;
        if (staticContentProvider.getStaticContent(url, file)) {
            // NOTE: Unfortunately, in this version of Spring the method getIfNotModifiedSince() has the wrong name
            long ifModifiedSince = request.getHeaders().getIfNotModifiedSince();
            if (ifModifiedSince > 0L && file.lastModified() - ifModifiedSince < 1000L) {
                responseCode = HttpStatus.NOT_MODIFIED;
            } else {
                responseCode = HttpStatus.OK;

                // Send file content as the request body
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

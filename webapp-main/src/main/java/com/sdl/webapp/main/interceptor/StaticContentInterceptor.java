package com.sdl.webapp.main.interceptor;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentProvider;
import com.sdl.webapp.common.api.WebRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Static content interceptor. This interceptor checks if the request is for static content, and if it is, it sends
 * an appropriate response to the client; in that case the request will not be processed further by Spring's
 * {@code DispatcherServlet} (it will not reach any of the controllers).
 *
 * This should be configured to be called after the {@code LocalizationResolverInterceptor} for requests that are
 * being handled by the Spring {@code DispatcherServlet}.
 */
@Component
public class StaticContentInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(StaticContentInterceptor.class);

    private static final int BUFFER_SIZE = 8192;

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Autowired
    private StaticContentProvider contentProvider;

    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Strip the protocol, domain, port and context path off of the URL
        final String url = urlPathHelper.getRequestUri(request).replace(urlPathHelper.getContextPath(request), "");
        LOG.trace("preHandle: {}", url);

        final Localization localization = webRequestContext.getLocalization();
        if (localization == null) {
            throw new IllegalStateException("Localization is not available. Please make sure that the " +
                    "LocalizationResolverInterceptor is registered and executed before the StaticContentInterceptor.");
        }

        if (localization.isStaticContent(url)) {
            LOG.debug("Handling static content: {}", url);
            final StaticContentItem staticContentItem = contentProvider.getStaticContent(url, localization.getId(),
                    localization.getPath());

            final ServletServerHttpRequest req = new ServletServerHttpRequest(request);
            final ServletServerHttpResponse res = new ServletServerHttpResponse(response);

            // NOTE: In this version of Spring, the method 'getIfNotModifiedSince' is named incorrectly
            if (staticContentItem.getLastModified() > req.getHeaders().getIfNotModifiedSince() - 1000L) {
                res.setStatusCode(HttpStatus.OK);
                writeResponseBody(staticContentItem, res);
            } else {
                res.setStatusCode(HttpStatus.NOT_MODIFIED);
            }

            res.close();
            return false;
        }

        return true;
    }

    private void writeResponseBody(StaticContentItem item, ServletServerHttpResponse response) throws IOException {
        try (final InputStream in = item.getContent(); final OutputStream out = response.getBody()) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
        }
    }
}

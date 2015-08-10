package com.sdl.webapp.main.interceptor;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.content.StaticContentProvider;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.util.MimeUtils;
import com.sdl.webapp.common.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Static content interceptor. This interceptor checks if the request is for static content, and if it is, it sends
 * an appropriate response to the client; in that case the request will not be processed further by Spring's
 * {@code DispatcherServlet} (it will not reach any of the controllers).
 *
 * This should be configured to be called after the {@code LocalizationResolverInterceptor} for requests that are
 * being handled by the Spring {@code DispatcherServlet}.
 */
public class StaticContentInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(StaticContentInterceptor.class);

    private final StaticContentProvider staticContentProvider;

    private final WebRequestContext webRequestContext;

    @Autowired
    public StaticContentInterceptor(StaticContentProvider staticContentProvider, WebRequestContext webRequestContext) {
        this.staticContentProvider = staticContentProvider;
        this.webRequestContext = webRequestContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("preHandle: {}", requestPath);

        final Localization localization = webRequestContext.getLocalization();
        if (localization == null) {
            throw new IllegalStateException("Localization is not available. Please make sure that the " +
                    "LocalizationResolverInterceptor is registered and executed before the StaticContentInterceptor.");
        }

        if (localization.isStaticContent(requestPath)) {
            LOG.debug("Handling static content: {}", requestPath);

            final ServletServerHttpRequest req = new ServletServerHttpRequest(request);
            final ServletServerHttpResponse res = new ServletServerHttpResponse(response);

            try {

                final StaticContentItem staticContentItem = staticContentProvider.getStaticContent(requestPath,
                        localization.getId(), localization.getPath());

                if (staticContentItem.getLastModified() > req.getHeaders().getIfModifiedSince() + 1000L) {
                    res.setStatusCode(HttpStatus.OK);
                    res.getHeaders().setLastModified(staticContentItem.getLastModified());
                    res.getHeaders().setContentType(MediaType.parseMediaType(staticContentItem.getContentType()));
                    try (final InputStream in = staticContentItem.getContent(); final OutputStream out = res.getBody()) {
                        StreamUtils.copy(in, out);
                    }
                } else {
                    res.setStatusCode(HttpStatus.NOT_MODIFIED);
                }
            }
            catch ( StaticContentNotFoundException e ) {
                LOG.debug("Static resource not found in static content provider. Fallback to webapp content...");

                URL contentResource = request.getServletContext().getResource(requestPath);
                if ( contentResource == null ) {
                    contentResource = request.getServletContext().getClassLoader().getResource(requestPath);
                }
                if ( contentResource != null ) {

                    res.setStatusCode(HttpStatus.OK);
                    // TODO: Set last modified on these resources as well!!
                    //res.getHeaders().setLastModified(...);

                    String mimeType = MimeUtils.getMimeType(contentResource);
                    res.getHeaders().setContentType(MediaType.parseMediaType(mimeType));

                    try (final InputStream in = contentResource.openStream(); final OutputStream out = res.getBody()) {
                        StreamUtils.copy(in, out);
                    }
                }
                else {
                    throw e;
                }
            }


            res.close();
            return false;
        }

        return true;
    }
}

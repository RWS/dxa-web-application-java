package com.sdl.webapp.common.impl.interceptor;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.util.MimeUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.Hours;
import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Static content interceptor. This interceptor checks if the request is for static content, and if it is, it sends
 * an appropriate response to the client; in that case the request will not be processed further by Spring's
 * {@link org.springframework.web.servlet.DispatcherServlet} (it will not reach any of the controllers).
 */
//todo dxa2 remove in preference of simple controller
public class StaticContentInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(StaticContentInterceptor.class);
    private static final String CACHE_CONTROL_WEEK = "public, max-age=" + Weeks.ONE.toStandardSeconds().getSeconds();
    private static final String CACHE_CONTROL_HOUR = "public, max-age=" + Hours.ONE.toStandardSeconds().getSeconds();
    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");

    @Autowired
    private ContentProvider contentProvider;

    @Autowired
    private WebRequestContext webRequestContext;

    private static boolean isToBeRefreshed(ServletServerHttpResponse res, long notModifiedSince, long lastModified, boolean isVersioned, boolean isPreview) {

        // If preview is enabled we never want to cache images as they may change after editing them
        if (isPreview){
            return true;            
        }

        if (isVersioned) {
            res.getHeaders().setCacheControl(CACHE_CONTROL_WEEK);
            res.getHeaders().setExpires(lastModified + Weeks.ONE.toStandardSeconds().getSeconds() * 1000L);
        } else {
            res.getHeaders().setCacheControl(CACHE_CONTROL_HOUR);
            res.getHeaders().setExpires(lastModified + Hours.ONE.toStandardSeconds().getSeconds() * 1000L);
        }
        res.getHeaders().setLastModified(lastModified);

        if (lastModified > notModifiedSince + 1000L) {
            res.setStatusCode(HttpStatus.OK);
            return true;
        } else {
            res.setStatusCode(HttpStatus.NOT_MODIFIED);
            return false;
        }
    }

    private static void fallbackForContentProvider(ServletServerHttpRequest req, String requestPath, ServletServerHttpResponse res, boolean isPreview)
            throws IOException, StaticContentNotFoundException {
        LOG.warn("Static resource not found in static content provider for " + requestPath + ". Fallback to webapp content...");

        URL contentResource = req.getServletRequest().getServletContext().getResource(requestPath);
        if (contentResource == null) {
            contentResource = req.getServletRequest().getServletContext().getClassLoader().getResource(requestPath);
        }

        if (contentResource == null) {
            return;
        }
        String mimeType = MimeUtils.getMimeType(contentResource);
        res.getHeaders().setContentType(MediaType.parseMediaType(mimeType));

        if (isToBeRefreshed(res, req.getHeaders().getIfModifiedSince(),
                ManagementFactory.getRuntimeMXBean().getStartTime(), false, isPreview)) {
            try (final InputStream in = contentResource.openStream();
                 final OutputStream out = res.getBody()) {
                IOUtils.copy(in, out);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        return preHandle(request, response, handler, webRequestContext.isPreview());
    }


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler, boolean isPreview) throws ServletException {
        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("preHandle: {}", requestPath);

        final Localization localization = webRequestContext.getLocalization();
        if (localization.isStaticContent(requestPath)) {
            LOG.debug("Handling static content: {}", requestPath);

            final ServletServerHttpRequest req = new ServletServerHttpRequest(request);

            try (final ServletServerHttpResponse res = new ServletServerHttpResponse(response)) {
                if (localization.isNonPublishedAsset(requestPath))
                {
                    fallbackForContentProvider(req, removeVersionNumber(requestPath), res, isPreview);
                }
                StaticContentItem staticContentItem = null;

                try {
                    staticContentItem = contentProvider.getStaticContent(requestPath, localization.getId(), localization.getPath());
                } catch (StaticContentNotFoundException e) {
                    fallbackForContentProvider(req, removeVersionNumber(requestPath), res, isPreview);
                }

                if (staticContentItem != null) {

                    res.getHeaders().setContentType(MediaType.parseMediaType(staticContentItem.getContentType()));

                    // http://stackoverflow.com/questions/1587667/should-http-304-not-modified-responses-contain-cache-control-headers
                    if (isToBeRefreshed(res, req.getHeaders().getIfModifiedSince(),
                            staticContentItem.getLastModified(), staticContentItem.isVersioned(), isPreview)) {
                        try (final InputStream in = staticContentItem.getContent(); final OutputStream out = res.getBody()) {
                            IOUtils.copy(in, out);
                        }
                    }
                }
            } catch (IOException | ContentProviderException e) {
                LOG.warn("Issues getting the static content {}", requestPath, e);
                throw new ServletException(e);
            }

            return false;
        }
        return true;
    }

    protected static String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }
}

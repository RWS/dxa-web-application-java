package com.sdl.webapp.common.impl.interceptor;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Localization resolver interceptor. This interceptor determines the localization for the request and stores it in the
 * {@code WebRequestContext} so that it is available for other components when processing the request.
 * <p>
 * This should be the first interceptor to be called for requests that are being handled by the Spring
 * {@code DispatcherServlet}.
 * </p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class LocalizationResolverInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(LocalizationResolverInterceptor.class);

    private static final int DEFAULT_PORT = 80;

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Autowired
    private LocalizationResolver localizationResolver;

    @Autowired
    private WebRequestContext webRequestContext;

    private static String getBaseUrl(HttpServletRequest request) {
        final StringBuilder sb = new StringBuilder(16)
                .append(request.getScheme()).append("://").append(request.getServerName());

        int port = request.getServerPort();
        if (port != DEFAULT_PORT) {
            sb.append(':').append(port);
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        webRequestContext.setBaseUrl(getBaseUrl(request));
        webRequestContext.setContextPath(urlPathHelper.getOriginatingContextPath(request));
        webRequestContext.setRequestPath(urlPathHelper.getOriginatingRequestUri(request));

        webRequestContext.setInclude(request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null);
        webRequestContext.setDeveloperMode(request.getServerName().contains("localhost"));

        // Check if the cookie set by CID is present
        webRequestContext.setContextCookiePresent(false);
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("context")) {
                    webRequestContext.setContextCookiePresent(true);
                    break;
                }
            }
        }

        final String fullUrl = webRequestContext.getFullUrl();
        LOG.trace("preHandle: {}", fullUrl);

        final Localization localization = localizationResolver.getLocalization(fullUrl);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Localization for {} is: [{}] {}",
                    fullUrl, localization.getId(), localization.getPath());
        }
        webRequestContext.setLocalization(localization);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        LOG.trace("afterCompletion: {}", request.getRequestURL().toString());
    }
}

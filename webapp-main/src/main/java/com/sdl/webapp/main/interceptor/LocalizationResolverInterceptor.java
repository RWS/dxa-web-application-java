package com.sdl.webapp.main.interceptor;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Localization resolver interceptor. This interceptor determines the localization for the request and stores it in the
 * {@code WebRequestContext} so that it is available for other components when processing the request.
 *
 * This should be the first interceptor to be called for requests that are being handled by the Spring
 * {@code DispatcherServlet}.
 */
public class LocalizationResolverInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(LocalizationResolverInterceptor.class);

    private static final int DEFAULT_PORT = 80;

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private final LocalizationResolver localizationResolver;

    private final WebRequestContext webRequestContext;

    @Autowired
    public LocalizationResolverInterceptor(LocalizationResolver localizationResolver, WebRequestContext webRequestContext) {
        this.localizationResolver = localizationResolver;
        this.webRequestContext = webRequestContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        final String baseUrl = getBaseUrl(request);
        final String requestPath = urlPathHelper.getOriginatingRequestUri(request);
        final String fullUrl = baseUrl + requestPath;
        LOG.trace("preHandle: {}", fullUrl);

        webRequestContext.setBaseUrl(baseUrl);
        webRequestContext.setRequestPath(requestPath);

        final Localization localization = localizationResolver.getLocalization(fullUrl);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Localization for {} is: [{}] {}",
                    new Object[] { fullUrl, localization.getId(), localization.getPath() });
        }
        webRequestContext.setLocalization(localization);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        LOG.trace("afterCompletion: {}", request.getRequestURL().toString());
    }

    private String getBaseUrl(HttpServletRequest request) {
        final StringBuilder sb = new StringBuilder()
                .append(request.getScheme()).append("://").append(request.getServerName());

        int port = request.getServerPort();
        if (port != DEFAULT_PORT) {
            sb.append(':').append(port);
        }

        return sb.append(request.getContextPath()).toString();
    }
}

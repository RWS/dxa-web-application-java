package com.sdl.webapp.main.interceptor;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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
        // NOTE: The full URL (including protocol, domain, port etc.) must be passed here
        final String url = request.getRequestURL().toString();
        LOG.trace("preHandle: {}", url);

        final Localization localization = localizationResolver.getLocalization(url);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Localization for {} is: [{}] {}",
                    new Object[] { url, localization.getId(), localization.getPath() });
        }
        webRequestContext.setLocalization(localization);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        LOG.trace("afterCompletion: {}", request.getRequestURL().toString());
    }
}

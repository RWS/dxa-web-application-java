package com.sdl.webapp.tridion.config;

import com.sdl.web.ambient.client.AmbientClientFilter;
import com.sdl.web.preview.client.filter.ClientBinaryContentFilter;
import com.sdl.web.preview.client.filter.ClientPageContentFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import static com.sdl.webapp.common.util.InitializationUtils.loadDxaProperties;
import static com.sdl.webapp.common.util.InitializationUtils.registerFilter;

@Slf4j
public class ExternalFiltersWebConfiguration implements WebApplicationInitializer, Ordered {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.trace("Registration for XPM and preview filters if needed...");
        if (Boolean.parseBoolean(loadDxaProperties().getProperty("dxa.web.xpm.enabled"))) {
            log.debug("dxa.web.xpm.enabled = true, thus registering XPM");
            registerXpm(servletContext);
        }

        if (Boolean.parseBoolean(loadDxaProperties().getProperty("dxa.web.adf.enabled"))) {
            log.debug("dxa.web.adf.enabled = true, thus registering ADF");
            registerAdf(servletContext);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private void registerXpm(ServletContext servletContext) {
        registerFilter(servletContext, ClientPageContentFilter.class, "/*");
        registerFilter(servletContext, ClientBinaryContentFilter.class, "/*");
        log.debug("XPM filters set is registered");
    }

    private void registerAdf(ServletContext servletContext) {
        registerFilter(servletContext, AmbientClientFilter.class, "/*");
        log.debug("ADF filter is registered");
    }
}

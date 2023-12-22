package com.sdl.webapp.tridion.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.WebApplicationInitializer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/*
import java.util.Properties;
import static com.sdl.webapp.common.util.InitializationUtils.loadDxaProperties;
import static com.sdl.webapp.common.util.InitializationUtils.registerFilter;
*/

@Slf4j
public class ExternalFiltersWebConfiguration implements WebApplicationInitializer, Ordered {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("XPM not currently supported with Spring Framework 6!");
        /*
        log.info("Registration for XPM and preview filters if needed...");
        Properties properties = loadDxaProperties();
        if (Boolean.parseBoolean(properties.getProperty("dxa.web.xpm.enabled"))) {
            log.info("dxa.web.xpm.enabled = true, thus registering XPM");
            registerXpm(servletContext);
        }
        if (Boolean.parseBoolean(properties.getProperty("dxa.web.adf.enabled"))) {
            log.info("dxa.web.adf.enabled = true, thus registering ADF");
            registerAdf(servletContext);
        }
        */
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /*
    private void registerXpm(ServletContext servletContext) {
        registerFilter(servletContext, ClientPageContentFilter.class, "/*");
        registerFilter(servletContext, ClientBinaryContentFilter.class, "/*");
        log.debug("XPM filters set is registered");
    }

    private void registerAdf(ServletContext servletContext) {
        registerFilter(servletContext, AmbientClientFilter.class, "/*");
        log.debug("ADF filter is registered");
    }
    */
}

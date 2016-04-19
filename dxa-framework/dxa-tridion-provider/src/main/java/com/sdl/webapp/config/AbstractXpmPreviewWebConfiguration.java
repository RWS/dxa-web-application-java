package com.sdl.webapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import static com.sdl.webapp.common.util.InitializationUtils.loadDxaProperties;

@Slf4j
public abstract class AbstractXpmPreviewWebConfiguration implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.trace("Registration for XPM and preview filters if needed...");
        if (Boolean.parseBoolean(loadDxaProperties().getProperty("dxa.web.xpm.enabled"))) {
            register(servletContext);
        }
    }

    protected abstract void register(ServletContext servletContext);
}

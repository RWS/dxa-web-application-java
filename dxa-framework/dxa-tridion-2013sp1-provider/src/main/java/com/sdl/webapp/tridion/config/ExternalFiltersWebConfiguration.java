package com.sdl.webapp.tridion.config;

import com.sdl.webapp.config.AbstractExternalSystemsFiltersWebConfiguration;
import com.tridion.ambientdata.web.AmbientDataServletFilter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;

import static com.sdl.webapp.common.util.InitializationUtils.registerFilter;

@Slf4j
public class ExternalFiltersWebConfiguration extends AbstractExternalSystemsFiltersWebConfiguration {
    @Override
    protected void registerXpm(ServletContext servletContext) {
        log.debug("XPM registration for 2013sp1 doesn't require any filters. Ensure that ADF is enabled!");
    }

    @Override
    protected void registerAdf(ServletContext servletContext) {
        registerFilter(servletContext, AmbientDataServletFilter.class, "/*");
        log.debug("ADF filter is registered");
    }
}

package com.sdl.webapp.tridion.config;

import com.sdl.web.ambient.client.AmbientClientFilter;
import com.sdl.web.preview.client.filter.ClientBinaryContentFilter;
import com.sdl.web.preview.client.filter.ClientPageContentFilter;
import com.sdl.webapp.config.AbstractExternalSystemsFiltersWebConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;

import static com.sdl.webapp.common.util.InitializationUtils.registerFilter;

@Slf4j
public class ExternalFiltersWebConfiguration extends AbstractExternalSystemsFiltersWebConfiguration {
    @Override
    protected void registerXpm(ServletContext servletContext) {
        registerFilter(servletContext, ClientPageContentFilter.class, "/*");
        registerFilter(servletContext, ClientBinaryContentFilter.class, "/*");
        log.debug("XPM filters set is registered");
    }

    @Override
    protected void registerAdf(ServletContext servletContext) {
        registerFilter(servletContext, AmbientClientFilter.class, "/*");
        log.debug("ADF filter is registered");
    }
}

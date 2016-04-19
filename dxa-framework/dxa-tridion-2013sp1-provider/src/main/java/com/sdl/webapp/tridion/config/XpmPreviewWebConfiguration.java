package com.sdl.webapp.tridion.config;

import com.sdl.webapp.config.AbstractXpmPreviewWebConfiguration;
import com.tridion.ambientdata.web.AmbientDataServletFilter;

import javax.servlet.ServletContext;

import static com.sdl.webapp.common.util.InitializationUtils.registerFilter;

public class XpmPreviewWebConfiguration extends AbstractXpmPreviewWebConfiguration {
    @Override
    protected void register(ServletContext servletContext) {
        registerFilter(servletContext, AmbientDataServletFilter.class, "/*");
    }
}

package com.sdl.webapp.tridion.config;

import com.sdl.web.ambient.client.AmbientClientFilter;
import com.sdl.web.preview.client.filter.ClientBinaryContentFilter;
import com.sdl.web.preview.client.filter.ClientPageContentFilter;
import com.sdl.webapp.config.AbstractXpmPreviewWebConfiguration;

import javax.servlet.ServletContext;

import static com.sdl.webapp.common.util.InitializationUtils.registerFilter;

public class XpmPreviewWebConfiguration extends AbstractXpmPreviewWebConfiguration {
    @Override
    protected void register(ServletContext servletContext) {
        registerFilter(servletContext, AmbientClientFilter.class, "/*");
        registerFilter(servletContext, ClientPageContentFilter.class, "/*");
        registerFilter(servletContext, ClientBinaryContentFilter.class, "/*");
    }
}

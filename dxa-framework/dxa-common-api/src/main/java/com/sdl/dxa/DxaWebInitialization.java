package com.sdl.dxa;

import com.sdl.webapp.common.impl.interceptor.HealthCheckFilter;
import com.sdl.webapp.common.util.InitializationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Optional;
import java.util.Properties;

import static com.sdl.webapp.common.util.InitializationUtils.registerListener;
import static com.sdl.webapp.common.util.InitializationUtils.registerServlet;

/**
 * <p>Web application initializer which configures the web application by registering default web configuration dor DXA.</p>
 * <p>
 * <p>Initialization and configuration for this web application is done purely in code, and not with a <code>web.xml</code>
 * deployment descriptor.</p>
 */
@Slf4j
public class DxaWebInitialization implements WebApplicationInitializer, Ordered {

    private static void registerWebServiceServlet(ServletContext servletContext) {
        ServletRegistration.Dynamic registration = registerServlet(servletContext,
                "com.sun.jersey.spi.container.servlet.ServletContainer", "/cd_preview_webservice/ws/*");

        if (registration == null) {
            return;
        }

        registration.setInitParameter("com.sun.jersey.config.property.resourceConfigClass",
                "com.sun.jersey.api.core.ClassNamesResourceConfig");
        registration.setInitParameter("com.sun.jersey.config.property.classnames",
                "com.tridion.webservices.odata.ODataWebservice;com.tridion.webservices.linking.LinkingService");
    }

    private static void registerCharacterEncodingFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic registration = InitializationUtils.registerFilter(servletContext, new CharacterEncodingFilter(), "/*");
        registration.setInitParameter("encoding", "UTF-8");
        registration.setInitParameter("forceEncoding", "true");
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        Properties dxaProperties = InitializationUtils.loadDxaProperties();

        if (Boolean.parseBoolean(dxaProperties.getProperty("dxa.web.default.init", "true"))) {

            registerWebServiceServlet(servletContext);
            registerCharacterEncodingFilter(servletContext);

            InitializationUtils.registerFilter(servletContext, HealthCheckFilter.class, "/system/health");

            registerServlet(servletContext, "com.tridion.transport.HTTPSReceiverServlet", "/cd_upload/httpupload");
            registerListener(servletContext, "com.tridion.storage.persistence.session.SessionManagementContextListener");
            registerListener(servletContext, "com.tridion.webservices.odata.ODataContextListener");

            log.info("Default DXA web application initialization complete.");

            // keep the "dxa.modules.cid.sessionid.name" for backwards-compatibility
            String sessionIdName = Optional.ofNullable(dxaProperties.getProperty("dxa.modules.cid.sessionid.name"))
                    .orElseGet(() -> Optional.ofNullable(dxaProperties.getProperty("dxa.web.sessionid.name"))
                            .orElse(null));
            if (sessionIdName != null) {
                servletContext.getSessionCookieConfig().setName(sessionIdName);
                log.info("Set default SESSIONID to {}", sessionIdName);
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

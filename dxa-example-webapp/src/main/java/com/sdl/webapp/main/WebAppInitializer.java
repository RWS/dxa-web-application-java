package com.sdl.webapp.main;

import com.sdl.dxa.DxaSpringInitialization;
import com.sdl.webapp.common.util.InitializationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import static com.sdl.webapp.common.util.InitializationUtils.registerListener;
import static com.sdl.webapp.common.util.InitializationUtils.registerServlet;

/**
 * <p>Web application initializer which configures the web application by registering listeners, filters and servlets.</p>
 * <p/>
 * <p>Initialization and configuration for this web application is done purely in code, and not with a {@code web.xml}
 * deployment descriptor. Doing it in code provides more flexibility; for example, this enables the web application
 * to automatically detect and register the Contextual Image Delivery {@code ImageTransformerServlet} if it is present
 * on the classpath.</p>
 */
@Slf4j
public class WebAppInitializer implements WebApplicationInitializer {
    private static void registerCharacterEncodingFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic registration = InitializationUtils.registerFilter(servletContext, new CharacterEncodingFilter(), "/*");
        registration.setInitParameter("encoding", "UTF-8");
        registration.setInitParameter("forceEncoding", "true");
    }

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

    private static void setupSpringContext(ServletContext servletContext) {
        log.debug("Initializing servlet application context");
        AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();
        servletAppContext.register(DxaSpringInitialization.class);

        log.debug("Registering Spring ContextLoaderListener");
        registerListener(servletContext, new ContextLoaderListener(servletAppContext));

        log.debug("Registering Spring DispatcherServlet");
        registerServlet(servletContext, new DispatcherServlet(servletAppContext), "/").setLoadOnStartup(1);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        setupSpringContext(servletContext);

        registerWebServiceServlet(servletContext);
        registerCharacterEncodingFilter(servletContext);

        registerServlet(servletContext, "com.tridion.transport.HTTPSReceiverServlet", "/cd_upload/httpupload");
        registerListener(servletContext, "com.tridion.storage.persistence.session.SessionManagementContextListener");
        registerListener(servletContext, "com.tridion.webservices.odata.ODataContextListener");

        log.info("Web application initialization complete.");
    }

}

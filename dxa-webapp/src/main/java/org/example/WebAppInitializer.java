package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import static com.sdl.webapp.common.util.InitializationUtils.loadActiveSpringProfiles;
import static com.sdl.webapp.common.util.InitializationUtils.registerListener;
import static com.sdl.webapp.common.util.InitializationUtils.registerServlet;

/**
 * Initializes the web application instead of <code>web.xml</code>.
 */
@Slf4j
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.debug("Initializing servlet application context");
        AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();

        // register webapp spring configuration
        servletAppContext.register(SpringInitializer.class);

        log.debug("Registering Spring ContextLoaderListener");
        registerListener(servletContext, new ContextLoaderListener(servletAppContext));

        log.debug("Registering Spring DispatcherServlet");
        registerServlet(servletContext, new DispatcherServlet(servletAppContext), "/").setLoadOnStartup(1);

        loadActiveSpringProfiles(servletContext, servletAppContext);
    }
}

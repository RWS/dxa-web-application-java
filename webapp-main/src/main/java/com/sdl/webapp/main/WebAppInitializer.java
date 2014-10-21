package com.sdl.webapp.main;

import com.tridion.ambientdata.web.AmbientDataServletFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * Web application initializer which configures the web application by registering servlets such as the Spring
 * {@code DispatcherServlet}.
 */
public class WebAppInitializer implements WebApplicationInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(WebAppInitializer.class);

    private static final String ROOT_APP_CONTEXT_CONFIG_LOCATION = "classpath*:/META-INF/spring-context.xml";

    private static final String AMBIENT_DATA_SERVLET_FILTER_NAME = "AmbientDataServletFilter";

    private static final String IMAGE_TRANSFORMER_SERVLET_NAME = "ImageTransformerServlet";
    private static final String IMAGE_TRANSFORMER_SERVLET_CLASS_NAME =
            "com.sdl.context.image.servlet.ImageTransformerServlet";
    private static final String IMAGE_TRANSFORMER_SERVLET_MAPPING = "/cid/*";

    private static final String DISPATCHER_SERVLET_NAME = "DispatcherServlet";
    private static final String DISPATCHER_SERVLET_MAPPING = "/";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        registerContextLoaderListener(servletContext);
        registerAmbientDataServletFilter(servletContext);
        registerImageTransformerServlet(servletContext);
        registerDispatcherServlet(servletContext);
    }

    private void registerContextLoaderListener(ServletContext servletContext) {
        LOG.debug("Initializing root application context");
        XmlWebApplicationContext rootAppContext = new XmlWebApplicationContext();
        rootAppContext.setConfigLocation(ROOT_APP_CONTEXT_CONFIG_LOCATION);

        LOG.debug("Registering ContextLoaderListener");
        servletContext.addListener(new ContextLoaderListener(rootAppContext));
    }

    private void registerAmbientDataServletFilter(ServletContext servletContext) {
        LOG.debug("Registering AmbientDataServletFilter");
        FilterRegistration.Dynamic registration = servletContext.addFilter(AMBIENT_DATA_SERVLET_FILTER_NAME,
                new AmbientDataServletFilter());
        registration.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), false, DISPATCHER_SERVLET_NAME);
    }

    private void registerImageTransformerServlet(ServletContext servletContext) {
        if (ClassUtils.isPresent(IMAGE_TRANSFORMER_SERVLET_CLASS_NAME, ClassUtils.getDefaultClassLoader())) {
            LOG.debug("Registering ImageTransformerServlet");
            ServletRegistration.Dynamic registration = servletContext.addServlet(IMAGE_TRANSFORMER_SERVLET_NAME,
                    IMAGE_TRANSFORMER_SERVLET_CLASS_NAME);
            registration.addMapping(IMAGE_TRANSFORMER_SERVLET_MAPPING);
        } else {
            LOG.debug("ImageTransformerServlet is not available.");
        }
    }

    private void registerDispatcherServlet(ServletContext servletContext) {
        LOG.debug("Initializing servlet application context");
        AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();
        servletAppContext.register(WebAppConfiguration.class);

        LOG.debug("Registering DispatcherServlet");
        ServletRegistration.Dynamic registration = servletContext.addServlet(DISPATCHER_SERVLET_NAME,
                new DispatcherServlet(servletAppContext));
        registration.setLoadOnStartup(1);
        registration.addMapping(DISPATCHER_SERVLET_MAPPING);
    }
}

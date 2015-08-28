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
 * Web application initializer which configures the web application by registering listeners, filters and servlets.
 *
 * Initialization and configuration for this web application is done purely in code, and not with a {@code web.xml}
 * deployment descriptor. Doing it in code provides more flexibility; for example, this enables the web application
 * to automatically detect and register the Contextual Image Delivery {@code ImageTransformerServlet} if it is present
 * on the classpath.
 */
public class WebAppInitializer implements WebApplicationInitializer {

    // TODO: Make this pluggable, so CID and other modules and attach servlet definitions?
    // Or just have CID as servlet fragment??
    // The problem is as soon you have stuff in web.xml etc those things are deployed first

    private static final Logger LOG = LoggerFactory.getLogger(WebAppInitializer.class);

    private static final String ROOT_APP_CONTEXT_CONFIG_LOCATION = "classpath*:/META-INF/spring-context.xml";

    private static final String AMBIENT_DATA_SERVLET_FILTER_NAME = "AmbientDataServletFilter";

    private static final String IMAGE_TRANSFORMER_SERVLET_NAME = "ImageTransformerServlet";
    private static final String IMAGE_TRANSFORMER_SERVLET_CLASS_NAME =
            "com.sdl.context.image.servlet.ImageTransformerServlet";
    private static final String IMAGE_TRANSFORMER_SERVLET_MAPPING = "/cid/*";

    private static final String DISPATCHER_SERVLET_NAME = "DispatcherServlet";
    private static final String DISPATCHER_SERVLET_MAPPING = "/";

    private static final String HTTP_UPLOAD_SERVLET_NAME = "HttpUpload";
    private static final String HTTP_UPLOAD_SERVLET_CLASS_NAME = "com.tridion.transport.HTTPSReceiverServlet";
    private static final String HTTP_UPLOAD_SERVLET_MAPPING = "/cd_upload/httpupload";

    private static final String WEB_SERVICE_SERVLET_NAME = "ContentDeliveryWebService";
    private static final String WEB_SERVICE_SERVLET_CLASS_NAME = "com.sun.jersey.spi.container.servlet.ServletContainer";
    private static final String WEB_SERVICE_SERVLET_MAPPING = "/cd_preview_webservice/ws/*";
    private static final String WEB_SERVICE_RESOURCE_CONFIG_CLASS_INIT_PARAM_NAME = "com.sun.jersey.config.property.resourceConfigClass";
    private static final String WEB_SERVICE_RESOURCE_CONFIG_CLASS_INIT_PARAM_VALUE = "com.sun.jersey.api.core.ClassNamesResourceConfig";
    private static final String WEB_SERVICE_CLASSNAMES_PARAM_NAME = "com.sun.jersey.config.property.classnames";
    private static final String WEB_SERVICE_CLASSNAMES_PARAM_VALUE = "com.tridion.webservices.odata.ODataWebservice;com.tridion.webservices.linking.LinkingService";

    private static final String WEB_SERVICE_LISTENER_CLASS_NAME = "com.tridion.webservices.odata.ODataContextListener";

    private static final String PREVIEW_SESSION_LISTENER_CLASS_NAME = "com.tridion.storage.persistence.session.SessionManagementContextListener";


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        registerHttpUploadServlet(servletContext);
        registerContextLoaderListener(servletContext);
        registerAmbientDataServletFilter(servletContext);
        registerImageTransformerServlet(servletContext);
        registerPreviewSessionListener(servletContext);
        registerWebServiceServlet(servletContext);
        registerWebServiceListener(servletContext);

        registerDispatcherServlet(servletContext);

        LOG.info("Web application initialization complete.");
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
        registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
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

    private void registerHttpUploadServlet(ServletContext servletContext) {
        if (ClassUtils.isPresent(HTTP_UPLOAD_SERVLET_CLASS_NAME, ClassUtils.getDefaultClassLoader())) {
            LOG.debug("Registering HttpUploadServlet");
            ServletRegistration.Dynamic registration = servletContext.addServlet(HTTP_UPLOAD_SERVLET_NAME, HTTP_UPLOAD_SERVLET_CLASS_NAME);
            registration.addMapping(HTTP_UPLOAD_SERVLET_MAPPING);
        }
    }

    private void registerPreviewSessionListener(ServletContext servletContext) {
        if (ClassUtils.isPresent(PREVIEW_SESSION_LISTENER_CLASS_NAME, ClassUtils.getDefaultClassLoader()) ) {
            LOG.debug("Registering Preview Session Listener");
            servletContext.addListener(PREVIEW_SESSION_LISTENER_CLASS_NAME);
        }
    }

    private void registerWebServiceServlet(ServletContext servletContext) {
        if (ClassUtils.isPresent(WEB_SERVICE_SERVLET_CLASS_NAME, ClassUtils.getDefaultClassLoader())) {
            LOG.debug("Registering Web Service Servlet");
            ServletRegistration.Dynamic registration = servletContext.addServlet(WEB_SERVICE_SERVLET_NAME, WEB_SERVICE_SERVLET_CLASS_NAME);
            registration.setInitParameter(WEB_SERVICE_RESOURCE_CONFIG_CLASS_INIT_PARAM_NAME, WEB_SERVICE_RESOURCE_CONFIG_CLASS_INIT_PARAM_VALUE);
            registration.setInitParameter(WEB_SERVICE_CLASSNAMES_PARAM_NAME, WEB_SERVICE_CLASSNAMES_PARAM_VALUE);
            registration.addMapping(WEB_SERVICE_SERVLET_MAPPING);
        }
    }

    private void registerWebServiceListener(ServletContext servletContext) {
        if (ClassUtils.isPresent(WEB_SERVICE_LISTENER_CLASS_NAME, ClassUtils.getDefaultClassLoader())) {
            LOG.debug("Registering Web Service Listener");
            servletContext.addListener(WEB_SERVICE_LISTENER_CLASS_NAME);
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

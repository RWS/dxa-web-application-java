package org.example;

import com.sdl.dxa.DxaSpringInitialization;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@Configuration
@ComponentScan(basePackages = {"com.sdl.dxa"})
public class WebAppConfiguration implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();
        servletAppContext.register(DxaSpringInitialization.class);

        servletContext.addListener(new ContextLoaderListener(servletAppContext));

        ServletRegistration.Dynamic registration = servletContext.addServlet("DispatcherServlet", new DispatcherServlet(servletAppContext));
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}
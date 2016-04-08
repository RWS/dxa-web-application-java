package com.sdl.webapp.common.impl.util.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import static com.sdl.webapp.common.util.InitializationUtils.registerFilter;

@Configuration
public class ServletResponseSpringConfig implements WebApplicationInitializer {

    @Bean
    public ServletResponseFactoryBean servletResponseFactoryBean() {
        return new ServletResponseFactoryBean(servletResponseInScopeFilter());
    }

    @Bean(name = "servletResponseInScopeFilter")
    public ServletResponseInScopeFilter servletResponseInScopeFilter() {
        return new ServletResponseInScopeFilter();
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        registerFilter(servletContext, new DelegatingFilterProxy("servletResponseInScopeFilter"), "/*");
    }
}

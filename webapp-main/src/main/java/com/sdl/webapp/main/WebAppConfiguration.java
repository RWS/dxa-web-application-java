package com.sdl.webapp.main;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.StaticContentProvider;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.main.interceptor.LocalizationResolverInterceptor;
import com.sdl.webapp.main.interceptor.StaticContentInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Configuration for the Spring servlet application context.
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.sdl.webapp.main")
public class WebAppConfiguration extends WebMvcConfigurerAdapter {

    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/view/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

    @Autowired
    private LocalizationResolver localizationResolver;

    @Autowired
    private StaticContentProvider staticContentProvider;

    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localizationResolverInterceptor());
        registry.addInterceptor(staticContentInterceptor());
    }

    @Bean
    public HandlerInterceptor localizationResolverInterceptor() {
        return new LocalizationResolverInterceptor(localizationResolver, webRequestContext);
    }

    @Bean
    public HandlerInterceptor staticContentInterceptor() {
        return new StaticContentInterceptor(staticContentProvider, webRequestContext);
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);
        return viewResolver;
    }
}

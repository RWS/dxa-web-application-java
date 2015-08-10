package com.sdl.webapp.main;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.StaticContentProvider;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.main.interceptor.LocalizationResolverInterceptor;
import com.sdl.webapp.main.interceptor.StaticContentInterceptor;
import com.sdl.webapp.main.interceptor.ThreadLocalInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
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
@ComponentScan(basePackages = {"com.sdl.webapp.main", "com.sdl.webapp.common.controller", "com.sdl.webapp.addon"})
public class WebAppConfiguration extends WebMvcConfigurerAdapter {

    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/Views/";
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
        registry.addInterceptor(threadLocalInterceptor());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // This is necessary to prevent "406 Not Acceptable" responses for certain types of files such as JavaScript,
        // CSS and images.
        // See http://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mvc.html#mvc-config-content-negotiation
        configurer.favorPathExtension(false).favorParameter(true);
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
    public HandlerInterceptor threadLocalInterceptor() {
        return new ThreadLocalInterceptor();
    }

    @Bean
    public ViewResolver viewResolver() {

        // TODO: Make it possible to hook in a contextual view resolver here?????
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);
        return viewResolver;
    }
}

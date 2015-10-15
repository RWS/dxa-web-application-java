package com.sdl.webapp.main;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.formats.DataFormatter;
import com.sdl.webapp.common.impl.interceptor.LocalizationResolverInterceptor;
import com.sdl.webapp.common.impl.interceptor.StaticContentInterceptor;
import com.sdl.webapp.common.impl.interceptor.ThreadLocalInterceptor;
import com.sdl.webapp.common.views.AtomView;
import com.sdl.webapp.common.views.RssView;
import com.sdl.webapp.common.views.JsonView;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Configuration for the Spring servlet application context.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.sdl.webapp.main", "com.sdl.webapp.common.controller", "com.sdl.webapp.addon"})
public class WebAppConfiguration extends WebMvcConfigurerAdapter {

    // TODO: This is a central class, why is this in the example webapp?? (NiC)
    // The example webapp should only be an example on how to assembly a webapp based on the different standard DXA modules.
    // Because now you are forced to copy stuff from this webapp to the customer specific one. From my point of view I would like to
    // have the example webapp as thin as possible, basically just config + module dependencies
    //

    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/Views/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

    @Autowired
    private LocalizationResolver localizationResolver;

    @Autowired
    private ContentProvider contentProvider;

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private DataFormatter dataFormatter;

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
        return new StaticContentInterceptor(contentProvider, webRequestContext);
    }

    @Bean
    public HandlerInterceptor threadLocalInterceptor() {
        return new ThreadLocalInterceptor();
    }

    @Bean
    public ViewResolver viewResolver() {

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);
        return viewResolver;
    }

    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return resolver;
    }

    @Bean(name = "rssFeedView")
    public RssView rssFeedView(){
        return new RssView(webRequestContext);
    }

    @Bean(name = "atomFeedView")
    public AtomView atomFeedView(){
        return new AtomView(webRequestContext);
    }

    @Bean(name = "jsonFeedView")
    public JsonView jsonFeedView(){ return new JsonView(webRequestContext); }
}
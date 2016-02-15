package com.sdl.webapp.common.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.webapp.common.impl.interceptor.LocalizationResolverInterceptor;
import com.sdl.webapp.common.impl.interceptor.StaticContentInterceptor;
import com.sdl.webapp.common.impl.interceptor.ThreadLocalInterceptor;
import com.sdl.webapp.common.views.AtomView;
import com.sdl.webapp.common.views.JsonView;
import com.sdl.webapp.common.views.RssView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
/**
 * <p>SpringConfiguration class.</p>
 */
@EnableWebMvc
@ImportResource({"classpath*:/META-INF/spring-context.xml"})
@ComponentScan(basePackages = {"com.sdl.webapp"})
public class SpringConfiguration extends WebMvcConfigurerAdapter {
    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/Views/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localizationResolverInterceptor());
        registry.addInterceptor(staticContentInterceptor());
        registry.addInterceptor(threadLocalInterceptor());
    }

    /**
     * <p>localizationResolverInterceptor.</p>
     *
     * @return a {@link org.springframework.web.servlet.HandlerInterceptor} object.
     */
    @Bean
    public HandlerInterceptor localizationResolverInterceptor() {
        return new LocalizationResolverInterceptor();
    }

    /**
     * <p>staticContentInterceptor.</p>
     *
     * @return a {@link org.springframework.web.servlet.HandlerInterceptor} object.
     */
    @Bean
    public HandlerInterceptor staticContentInterceptor() {
        return new StaticContentInterceptor();
    }

    /**
     * <p>threadLocalInterceptor.</p>
     *
     * @return a {@link org.springframework.web.servlet.HandlerInterceptor} object.
     */
    @Bean
    public HandlerInterceptor threadLocalInterceptor() {
        return new ThreadLocalInterceptor();
    }

    /**
     * <p>viewResolver.</p>
     *
     * @return a {@link org.springframework.web.servlet.ViewResolver} object.
     */
    @Bean
    public ViewResolver viewResolver() {

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);
        return viewResolver;
    }

    /**
     * <p>beanNameViewResolver.</p>
     *
     * @return a {@link org.springframework.web.servlet.view.BeanNameViewResolver} object.
     */
    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return resolver;
    }

    /**
     * <p>rssFeedView.</p>
     *
     * @return a {@link com.sdl.webapp.common.views.RssView} object.
     */
    @Bean(name = "rssFeedView")
    public RssView rssFeedView() {
        return new RssView();
    }

    /**
     * <p>atomFeedView.</p>
     *
     * @return a {@link com.sdl.webapp.common.views.AtomView} object.
     */
    @Bean(name = "atomFeedView")
    public AtomView atomFeedView() {
        return new AtomView();
    }

    /**
     * <p>jsonFeedView.</p>
     *
     * @return a {@link com.sdl.webapp.common.views.JsonView} object.
     */
    @Bean(name = "jsonFeedView")
    public JsonView jsonFeedView() {
        JsonView jsonView = new JsonView();
        jsonView.setExtractValueFromSingleKeyModel(true);
        return jsonView;
    }

    /**
     * <p>objectMapper.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.ObjectMapper} object.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}

package com.sdl.dxa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.webapp.common.views.AtomView;
import com.sdl.webapp.common.views.JsonView;
import com.sdl.webapp.common.views.RssView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * <p>Entry point for Spring initialization for DXA Framework which also triggers initialization for default paths
 * for parts of DXA and modules.</p>
 * <p>If imported to a custom Spring context, fully initializes DXA.</p>
 */
@Configuration
//todo dxa2 rename com.sdl.webapp to com.sdl.dxa
@ComponentScan(basePackages = {"com.sdl.webapp", "com.sdl.dxa"})
//TODO dxa2 remove resource auto-import
@ImportResource("classpath*:/META-INF/spring-context.xml")
@Slf4j
public class DxaSpringInitialization {

    private static void trace(Object bean) {
        log.trace("Bean initialization: {}", bean);
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("dxa.defaults.properties"));
        trace(configurer);
        return configurer;
    }

    @Configuration
    //TODO dxa2 consider moving web-defaults (e.g. view resolvers) to a different configuration
    public static class DxaWebSpringInitialization {
        @Value("${dxa.web.views.prefix}")
        private String viewResolverPrefix;

        @Value("${dxa.web.views.suffix}")
        private String viewResolverSuffix;

        @Bean
        public ViewResolver viewResolver() {
            InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
            viewResolver.setViewClass(JstlView.class);
            viewResolver.setPrefix(viewResolverPrefix);
            viewResolver.setSuffix(viewResolverSuffix);
            trace(viewResolver);
            return viewResolver;
        }

        @Bean
        public BeanNameViewResolver beanNameViewResolver() {
            BeanNameViewResolver resolver = new BeanNameViewResolver();
            resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
            trace(resolver);
            return resolver;
        }

        @Bean(name = "rssFeedView")
        public RssView rssFeedView() {
            RssView rssView = new RssView();
            trace(rssView);
            return rssView;
        }

        @Bean(name = "atomFeedView")
        public AtomView atomFeedView() {
            AtomView atomView = new AtomView();
            trace(atomView);
            return atomView;
        }

        @Bean(name = "jsonFeedView")
        public JsonView jsonFeedView() {
            JsonView jsonView = new JsonView();
            jsonView.setExtractValueFromSingleKeyModel(true);
            trace(jsonView);
            return jsonView;
        }

        /**
         * <p>Object mapper to be used across DXA.</p>
         */
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.registerModule(new JodaModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            trace(objectMapper);
            return objectMapper;
        }
    }
}

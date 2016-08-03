package com.sdl.dxa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.common.util.InitializationUtils;
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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Locale;

import static com.sdl.webapp.common.util.InitializationUtils.loadDxaProperties;
import static com.sdl.webapp.common.util.InitializationUtils.traceBeanInitialization;

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
//TODO dxa2 consider moving web-defaults (e.g. view resolvers) to a different configuration
public class DxaSpringInitialization {

    @Value("${dxa.web.views.prefix}")
    private String viewResolverPrefix;

    @Value("${dxa.web.views.suffix}")
    private String viewResolverSuffix;

    @Value("${dxa.web.views.override.folder}")
    private String viewResolverOverride;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(InitializationUtils.loadDxaProperties());
        traceBeanInitialization(configurer);
        return configurer;
    }

    @Bean
    public ViewResolver fallbackViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(viewResolverPrefix);
        viewResolver.setSuffix(viewResolverSuffix);
        viewResolver.setOrder(Ordered.LOWEST_PRECEDENCE);
        return viewResolver;
    }

    @Bean
    public ViewResolver dxaViewResolver() {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver() {

            @Override
            public View resolveViewName(String viewName, Locale locale) throws Exception {
                viewName = processDeviceFamily(viewName);

                View overriddenView = super.resolveViewName(viewResolverOverride + "/" + viewName, locale);
                if (null != overriddenView) {
                    log.debug("Found overridden view for {}, using it", viewName);
                    return overriddenView;
                }

                return super.resolveViewName(viewName, locale);
            }

            private String processDeviceFamily(String viewName) {
                ContextEngine contextEngine = ApplicationContextHolder.getContext().getBean(ContextEngine.class);
                String deviceFamily = contextEngine.getDeviceFamily();
                if (!"desktop".equals(deviceFamily)) {
                    viewName = viewName + "." + deviceFamily;
                    log.debug("ViewName is changed to {} and current device family is {}", viewName, deviceFamily);
                }
                return viewName;
            }
        };
        viewResolver.setViewClass(OptionalJstlView.class);
        viewResolver.setOrder(1);
        viewResolver.setPrefix(viewResolverPrefix);
        viewResolver.setSuffix(viewResolverSuffix);
        return viewResolver;
    }

    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        traceBeanInitialization(resolver);
        return resolver;
    }

    @Bean(name = "rssFeedView")
    public RssView rssFeedView() {
        RssView rssView = new RssView();
        traceBeanInitialization(rssView);
        return rssView;
    }

    @Bean(name = "atomFeedView")
    public AtomView atomFeedView() {
        AtomView atomView = new AtomView();
        traceBeanInitialization(atomView);
        return atomView;
    }

    @Bean(name = "jsonFeedView")
    public JsonView jsonFeedView() {
        JsonView jsonView = new JsonView();
        jsonView.setExtractValueFromSingleKeyModel(true);
        traceBeanInitialization(jsonView);
        return jsonView;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        traceBeanInitialization(objectMapper);
        return objectMapper;
    }

    private static class OptionalJstlView extends JstlView {

        @Override
        public boolean checkResource(Locale locale) throws Exception {
            return new ClassPathResource(loadDxaProperties().getProperty("dxa.web.views.folder") + this.getUrl()).exists()
                    || new ClassPathResource(this.getUrl()).exists();
        }
    }
}

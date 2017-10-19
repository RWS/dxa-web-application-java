package com.sdl.dxa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.api.serialization.json.DxaViewModelJsonChainFilter;
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
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Locale;

import static com.sdl.webapp.common.api.serialization.json.DxaViewModelJsonChainFilter.FILTER_NAME;
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
@Slf4j
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
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(new PropertiesPropertySource("dxa.properties.merged", InitializationUtils.loadDxaProperties()));
        configurer.setPropertySources(propertySources);
        configurer.setNullValue("null");
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
        final UrlBasedViewResolver viewResolver = new UrlBasedViewResolver() {

            @Override
            public View resolveViewName(String viewName, Locale locale) throws Exception {
                if (viewName.startsWith(FORWARD_URL_PREFIX) || viewName.startsWith(REDIRECT_URL_PREFIX)) {
                    return super.resolveViewName(viewName, locale);
                }

                View view = tryViewNames(locale, processDeviceFamily(viewName), viewResolverOverride + "/" + viewName);

                return view != null ? view : super.resolveViewName(viewName, locale);
            }

            private View tryViewNames(Locale locale, String... viewNames) throws Exception {
                for (String viewName : viewNames) {
                    View view = super.resolveViewName(viewName, locale);
                    if (null != view) {
                        log.debug("Found view name {}, using it", viewName);
                        return view;
                    }
                }
                return null;
            }

            private String processDeviceFamily(String viewName) {
                ContextEngine contextEngine = ApplicationContextHolder.getContext().getBean(ContextEngine.class);
                String deviceFamily = contextEngine.getDeviceFamily();
                log.debug("Current device family is {}", deviceFamily);
                return "desktop".equals(deviceFamily) ? viewName : (viewName + "." + deviceFamily);
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
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.registerModule(new JodaModule());
        objectMapper.setFilterProvider(jsonFilterProvider());
        objectMapper.setDateFormat(new StdDateFormat());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.UpperCamelCaseStrategy());
        traceBeanInitialization(objectMapper);
        return objectMapper;
    }

    @Bean
    public FilterProvider jsonFilterProvider() {
        SimpleFilterProvider provider = new SimpleFilterProvider();
        return provider.addFilter(FILTER_NAME, dxaViewModelJsonChainFilter());
    }

    @Bean
    public DxaViewModelJsonChainFilter dxaViewModelJsonChainFilter() {
        return new DxaViewModelJsonChainFilter();
    }

    private static class OptionalJstlView extends JstlView {

        @Override
        public boolean checkResource(Locale locale) throws Exception {
            String aroundWebapp = "../.." + this.getUrl();
            if (Thread.currentThread().getContextClassLoader().getResource(aroundWebapp) != null) {
                log.trace("Resolved view {} in a WebApp", aroundWebapp);
                return true;
            }

            String inModule = loadDxaProperties().getProperty("dxa.web.views.folder") + this.getUrl();
            if (new ClassPathResource(inModule).exists()) {
                log.trace("Resolved view {} in a JAR module", inModule);
                return true;
            }

            if (new ClassPathResource(this.getUrl()).exists()) {
                log.trace("Resolved view {} in a classpath", this.getUrl());
                return true;
            }

            log.trace("Cannot resolve view {}", this.getUrl());
            return false;
        }
    }
}

package com.sdl.dxa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sdl.dxa.api.datamodel.Constants;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;
import com.sdl.dxa.api.datamodel.json.ModelDataTypeIdResolver;
import com.sdl.dxa.api.datamodel.json.ModelDataTypeResolver;
import com.sdl.dxa.api.datamodel.json.Polymorphic;
import com.sdl.dxa.api.datamodel.json.PolymorphicObjectMixin;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.api.serialization.json.DxaViewModelJsonChainFilter;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.common.util.InitializationUtils;
import com.sdl.webapp.common.views.AtomView;
import com.sdl.webapp.common.views.JsonView;
import com.sdl.webapp.common.views.RssView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Locale;

import static com.sdl.webapp.common.api.serialization.json.DxaViewModelJsonChainFilter.FILTER_NAME;
import static com.sdl.webapp.common.util.InitializationUtils.traceBeanInitialization;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;

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
    public ViewResolver forwardRedirectViewResolver() {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver() {
            @Override
            public View resolveViewName(String viewName, Locale locale) throws Exception {
                if (viewName.startsWith(FORWARD_URL_PREFIX) || viewName.startsWith(REDIRECT_URL_PREFIX)) {
                    return super.resolveViewName(viewName, locale);
                }
                return null;
            }
        };
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setOrder(0);
        viewResolver.setPrefix(viewResolverPrefix);
        viewResolver.setSuffix(viewResolverSuffix);
        return viewResolver;
    }

    @Bean
    @Profile("!dxa.docs.enabled")
    public ViewResolver overrideDeviceContextualViewResolver() {
        UrlBasedViewResolver viewResolver = new ContextualDeviceUrlBasedViewResolver();
        viewResolver.setViewClass(OptionalJstlView.class);
        viewResolver.setOrder(10);
        viewResolver.setPrefix(viewResolverPrefix + viewResolverOverride);
        viewResolver.setSuffix(viewResolverSuffix);
        return viewResolver;
    }

    @Bean
    @Profile("!dxa.docs.enabled")
    public ViewResolver deviceContextualViewResolver() {
        UrlBasedViewResolver viewResolver = new ContextualDeviceUrlBasedViewResolver();
        viewResolver.setViewClass(OptionalJstlView.class);
        viewResolver.setOrder(20);
        viewResolver.setPrefix(viewResolverPrefix);
        viewResolver.setSuffix(viewResolverSuffix);
        return viewResolver;
    }

    @Bean
    public ViewResolver overrideViewResolver() {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        viewResolver.setViewClass(OptionalJstlView.class);
        viewResolver.setOrder(30);
        viewResolver.setPrefix(viewResolverPrefix + viewResolverOverride);
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

    @Bean
    public ViewResolver fallbackViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(viewResolverPrefix);
        viewResolver.setSuffix(viewResolverSuffix);
        viewResolver.setOrder(Ordered.LOWEST_PRECEDENCE);
        return viewResolver;
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

        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setFilterProvider(jsonFilterProvider());
        objectMapper.setDateFormat(new StdDateFormat());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.UpperCamelCaseStrategy());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Polymorphic.class));
        scanner.findCandidateComponents(DataModelSpringConfiguration.class.getPackage().getName())
                .forEach(type -> {
                    try {
                        Class<?> aClass = forName(type.getBeanClassName(), getDefaultClassLoader());
                        objectMapper.addMixIn(aClass, PolymorphicObjectMixin.class);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Class not found while mapping model data to typeIDs. Should never happen.", e);
                    }
                });
        objectMapper.addMixIn(Object.class, PolymorphicObjectMixin.class);

        ModelDataTypeResolver mapResolverBuilder = new ModelDataTypeResolver();
        mapResolverBuilder.init(JsonTypeInfo.Id.CUSTOM, new ModelDataTypeIdResolver());
        mapResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
        mapResolverBuilder.typeProperty(Constants.DOLLAR_TYPE);
        mapResolverBuilder.typeIdVisibility(true);
        objectMapper.setDefaultTyping(mapResolverBuilder);

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

    private static class ContextualDeviceUrlBasedViewResolver extends UrlBasedViewResolver {

        private String processDeviceFamily(String viewName) {
            ContextEngine contextEngine = ApplicationContextHolder.getContext().getBean(ContextEngine.class);
            String deviceFamily = contextEngine.getDeviceFamily();
            log.debug("Current device family is {}", deviceFamily);
            return "desktop".equals(deviceFamily) ? viewName : (viewName + "." + deviceFamily);
        }

        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            return super.resolveViewName(processDeviceFamily(viewName), locale);
        }


    }

    private static class OptionalJstlView extends JstlView {

        @Override
        public boolean checkResource(Locale locale) throws Exception {
            boolean exists = ApplicationContextHolder.getContext().getResource(this.getUrl()).exists();
            if (exists) {
                log.debug("Found view in application context: {}", this.getUrl());
            }
            return exists;
        }
    }

    @Bean
    @Profile("!dxa.generictopic.disabled")
    public IshModuleInitializer ishModuleInitializer() {
	    return new IshModuleInitializer();
    }
}

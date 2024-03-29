package com.sdl.dxa.api.datamodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sdl.dxa.api.datamodel.json.ModelDataTypeIdResolver;
import com.sdl.dxa.api.datamodel.json.ModelDataTypeResolver;
import com.sdl.dxa.api.datamodel.json.Polymorphic;
import com.sdl.dxa.api.datamodel.json.PolymorphicObjectMixin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;

/**
 * Module's Spring configuration.
 *
 */
@Configuration
@ComponentScan("com.sdl.dxa.api.datamodel")
@Slf4j
public class DataModelSpringConfiguration {

    @Bean
    public ObjectMapper dxaR2ObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.UpperCamelCaseStrategy());
        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new StdDateFormat());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Polymorphic.class));
        scanner.findCandidateComponents(DataModelSpringConfiguration.class.getPackage().getName())
                .forEach(type -> {
                    try {
                        Class<?> aClass = forName(type.getBeanClassName(), getDefaultClassLoader());
                        objectMapper.addMixIn(aClass, PolymorphicObjectMixin.class);
                    } catch (ClassNotFoundException e) {
                        log.warn("Class not found while mapping model data to typeIDs. Should never happen.", e);
                    }
                });
        objectMapper.addMixIn(Object.class, PolymorphicObjectMixin.class);

        ModelDataTypeResolver mapResolverBuilder = new ModelDataTypeResolver();
        mapResolverBuilder.init(JsonTypeInfo.Id.CUSTOM, new ModelDataTypeIdResolver());
        mapResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
        mapResolverBuilder.typeProperty(Constants.DOLLAR_TYPE);
        mapResolverBuilder.typeIdVisibility(true);
        objectMapper.setDefaultTyping(mapResolverBuilder);

        return objectMapper;
    }
}

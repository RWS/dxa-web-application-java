package com.sdl.dxa.api.datamodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.dxa.api.datamodel.json.PolymorphicObjectMixin;
import com.sdl.dxa.api.datamodel.json.TaxonomiesMixin;
import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.sdl.dxa.api.datamodel")
public class DataModelSpringConfiguration {

    @Bean
    public ObjectMapper dxaR2ObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(Object.class, PolymorphicObjectMixin.class);
        objectMapper.addMixIn(SitemapItemModelData.class, TaxonomiesMixin.class);
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.UpperCamelCaseStrategy());
        objectMapper.registerModule(new JodaModule());
        objectMapper.setDateFormat(new StdDateFormat());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        return objectMapper;
    }
}

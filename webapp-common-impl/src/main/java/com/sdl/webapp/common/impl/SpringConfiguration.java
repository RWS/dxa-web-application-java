package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.impl.mapping.SemanticMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.sdl.webapp.common.impl")
public class SpringConfiguration {

    @Bean
    public SemanticMapperImpl semanticMapperImpl() {
        return new SemanticMapperImpl(AbstractEntity.class.getPackage().getName());
    }
}

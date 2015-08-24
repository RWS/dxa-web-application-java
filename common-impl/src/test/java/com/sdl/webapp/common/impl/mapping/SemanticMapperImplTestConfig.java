package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for {@code SemanticMapperImplTest}.
 */
@Configuration
public class SemanticMapperImplTestConfig {

    @Bean
    public SemanticMapperImpl semanticMapperImpl() {
        return new SemanticMapperImpl(semanticMappingRegistry());
    }

    public SemanticMappingRegistry semanticMappingRegistry() {
        SemanticMappingRegistryImpl semanticMappingRegistry = new SemanticMappingRegistryImpl();
        semanticMappingRegistry.registerEntities(AbstractEntity.class.getPackage().getName());
        return semanticMappingRegistry;
    }
}

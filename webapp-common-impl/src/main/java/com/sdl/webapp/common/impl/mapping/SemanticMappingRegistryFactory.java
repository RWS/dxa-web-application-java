package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Spring factory bean to create the {@code SemanticMappingRegistry} bean.
 */
@Component
public class SemanticMappingRegistryFactory extends AbstractFactoryBean<SemanticMappingRegistry> {

    @Override
    public Class<?> getObjectType() {
        return SemanticMappingRegistry.class;
    }

    @Override
    protected SemanticMappingRegistry createInstance() throws Exception {
        SemanticMappingRegistryImpl semanticMappingRegistry = new SemanticMappingRegistryImpl();

        // Register all entity classes in the same package (and subpackages) as AbstractEntity
        semanticMappingRegistry.registerEntities(AbstractEntity.class.getPackage().getName());

        return semanticMappingRegistry;
    }
}

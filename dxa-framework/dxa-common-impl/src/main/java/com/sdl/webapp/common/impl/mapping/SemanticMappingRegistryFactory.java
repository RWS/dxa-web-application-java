package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Spring factory bean to create the {@code SemanticMappingRegistry} bean.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@Component
@Primary
public class SemanticMappingRegistryFactory extends AbstractFactoryBean<SemanticMappingRegistry> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getObjectType() {
        return SemanticMappingRegistry.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SemanticMappingRegistry createInstance() throws Exception {
        SemanticMappingRegistryImpl semanticMappingRegistry = new SemanticMappingRegistryImpl();

        // Register all entity classes in the same package (and subpackages) as AbstractEntity
        semanticMappingRegistry.registerEntities(AbstractEntityModel.class.getPackage().getName());

        return semanticMappingRegistry;
    }
}

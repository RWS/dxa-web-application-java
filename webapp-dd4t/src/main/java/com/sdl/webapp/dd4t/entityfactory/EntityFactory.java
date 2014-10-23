package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.model.Entity;
import org.dd4t.contentmodel.ComponentPresentation;

/**
 * Entity factory.
 */
public interface EntityFactory {

    /**
     * Returns the entity types that this factory can create.
     *
     * @return The entity types that this factory can create.
     */
    Class<?>[] supportedEntityTypes();

    /**
     * Creates an entity of the specified type from a component presentation.
     *
     * @param componentPresentation The component presentation.
     * @param entityClass The type of the entity to create.
     * @return The new entity.
     * @throws ContentProviderException If an error occurs and the entity cannot be created.
     */
    Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityClass) throws ContentProviderException;
}

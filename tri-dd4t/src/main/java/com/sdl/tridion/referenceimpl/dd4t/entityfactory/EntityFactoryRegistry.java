package com.sdl.tridion.referenceimpl.dd4t.entityfactory;

/**
 * Registry for entity factories.
 */
public interface EntityFactoryRegistry {

    /**
     * Gets an {@code EntityFactory} for entities of the specified type.
     *
     * @param entityType The entity type.
     * @return An {@code EntityFactory} for entities of the specified type.
     */
    EntityFactory getFactoryFor(Class<?> entityType);
}

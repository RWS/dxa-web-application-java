package com.sdl.webapp.dd4t.entityfactory;

/**
 * Registry for entity factories.
 */
public interface EntityFactoryRegistry {

    /**
     * Gets an {@code EntityFactory} for entities of the specified type.
     *
     * @param entityClass The entity type.
     * @return An {@code EntityFactory} for entities of the specified type.
     * @throws UnsupportedEntityTypeException If there is no factory that can create entities of the specified type.
     */
    EntityFactory getFactoryFor(Class<?> entityClass) throws UnsupportedEntityTypeException;
}

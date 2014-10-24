package com.sdl.webapp.dd4t.entityfactory;

/**
 * Registry for entity factories.
 */
public interface DD4TEntityFactoryRegistry {

    /**
     * Gets an {@code EntityFactory} for entities of the specified type.
     *
     * @param entityClass The entity type.
     * @return An {@code EntityFactory} for entities of the specified type.
     * @throws UnsupportedEntityTypeException If there is no factory that can create entities of the specified type.
     */
    DD4TEntityFactory getFactoryFor(Class<?> entityClass) throws UnsupportedEntityTypeException;
}

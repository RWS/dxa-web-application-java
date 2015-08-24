package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.model.entity.AbstractEntity;

/**
 * Registry that maps view names to view model object types.
 */
public interface ViewModelRegistry {

    /**
     * Registers an entity type for a view.
     *
     * @param viewName The name of the entity view.
     */
    void registerViewEntityClass(String viewName, Class<? extends AbstractEntity> entityClass);


    /**
     * Returns the entity type to use for a specified entity view.
     *
     * @param viewName The name of the entity view.
     * @return The type of the entity that this entity view needs.
     */
    Class<? extends AbstractEntity> getViewEntityClass(String viewName);
}

package com.sdl.webapp.common.config;

import com.sdl.webapp.common.model.Entity;

/**
 * Registry that maps view names to view model object types.
 */
public interface ViewModelRegistry {

    /**
     * Returns the entity type to use for a specified entity view.
     *
     * @param viewName The name of the entity view.
     * @return The type of the entity that this entity view needs.
     */
    Class<? extends Entity> getEntityViewModelType(String viewName);
}

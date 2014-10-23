package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.model.Entity;

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
    Class<? extends Entity> getViewEntityClass(String viewName);
}

package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;

/**
 * Registry that maps view names to view model object types.
 */
public interface ViewModelRegistry {

    /**
     * Registers an entity type for a view.
     *
     * @param viewName The name of the entity view.
     */
    void registerViewEntityClass(String viewName, Class<? extends AbstractEntityModel> entityClass);


    /**
     * Returns the entity type to use for a specified entity view.
     *
     * @param viewName The name of the entity view.
     * @return The type of the entity that this entity view needs.
     */
    Class<? extends AbstractEntityModel> getViewEntityClass(String viewName);

    /**
     * Returns the entity type to use for a sepecific semantic type
     *
     * @param viewNsemanticTypeNameame The name of the semantic type.
     * @return The type of the entity that this semantic type needs.
     */
    Class<? extends AbstractEntityModel> GetMappedModelTypes(String semanticTypeName);
}

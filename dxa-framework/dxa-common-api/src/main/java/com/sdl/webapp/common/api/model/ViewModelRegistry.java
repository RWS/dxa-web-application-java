package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.exceptions.DxaException;

import java.util.Set;

/**
 * Registry that maps view names to view model object types.
 */
public interface ViewModelRegistry {
    /**
     * Returns the entity type to use for a specified entity view.
     *
     * @param viewName The name of the entity view.
     * @return The type of the entity that this entity view needs.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    Class<? extends ViewModel> getViewEntityClass(String viewName) throws DxaException;


    /**
     * <p>getMappedModelTypes.</p>
     *
     * @param semanticTypeNames a {@link java.util.Set} object.
     * @return a {@link java.lang.Class} object.
     */
    Class<? extends ViewModel> getMappedModelTypes(Set<String> semanticTypeNames);

    /**
     * Returns the entity type to use for a sepecific semantic type
     *
     * @param semanticTypeName The name of the semantic type.
     * @return The type of the entity that this semantic type needs.
     */
    Class<? extends ViewModel> getMappedModelTypes(String semanticTypeName);

    /**
     * <p>getViewModelType.</p>
     *
     * @param regionMvcData a {@link com.sdl.webapp.common.api.model.MvcData} object.
     * @return a {@link java.lang.Class} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    Class<? extends ViewModel> getViewModelType(MvcData regionMvcData) throws DxaException;

    /**
     * Registers an entity type for a view.
     *
     * @param viewData    The name of the entity view.
     * @param entityClass The class to register
     */
    void registerViewModel(MvcData viewData, Class<? extends ViewModel> entityClass);

}

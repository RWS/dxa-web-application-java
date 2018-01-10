package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.exceptions.DxaException;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Registry that maps view names to view model object types.
 * @dxa.publicApi
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
     * @param expectedClass     expected class of an {@link EntityModel}, or {@code null} if you don't have expectation or have anything but {@link EntityModel}
     */
    Class<? extends ViewModel> getMappedModelTypes(Set<String> semanticTypeNames, @Nullable Class<? extends EntityModel> expectedClass) throws DxaException;

    /**
     * @deprecated since 2.0
     */
    @Deprecated
    Class<? extends ViewModel> getMappedModelTypes(Set<String> semanticTypeNames);

    /**
     * Returns the entity type to use for a specific semantic type
     *
     * @param semanticTypeName The name of the semantic type.
     * @param expectedClass    expected class of an {@link EntityModel}, or {@code null} if you don't have expectation or have anything but {@link EntityModel}
     * @return The type of the entity that this semantic type needs.
     */
    Class<? extends ViewModel> getMappedModelTypes(String semanticTypeName, @Nullable Class<? extends EntityModel> expectedClass) throws DxaException;

    /**
     * @deprecated since 2.0
     */
    @Deprecated
    Class<? extends ViewModel> getMappedModelTypes(String semanticTypeName);

    Class<? extends ViewModel> getViewModelType(MvcData regionMvcData) throws DxaException;

    /**
     * Registers an entity type for a view.
     *
     * @param viewData    The name of the entity view.
     * @param entityClass The class to register
     */
    void registerViewModel(MvcData viewData, Class<? extends ViewModel> entityClass);

}

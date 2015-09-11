package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;

/**
 * Registry that maps view names to view model object types.
 */
public interface ViewModelRegistry {
//
//    /**
//      * Registers an entity type for a view.
//      *
//      * @param viewData The name of the entity view.
//      * @param entityClass The class to register
//      */
//     void registerViewModel(MvcData viewData,  Class<? extends AbstractEntityModel> entityClass);
//
//    /**
//     * Registers an entity type for a view.
//     *
//     * @param viewData The name of the entity view.
//     * @param viewVirtualPath The virtual path of the view to infer the model from
//     */
//     void registerViewModel(MvcData viewData,  String viewVirtualPath);
//
//    /**
//     * gets an entity type for a view.
//     *
//     * @param viewData The name of the entity view.
//     */
//     Class<? extends AbstractEntityModel>  getViewModelType(MvcData viewData);
//
//    /**
//     * Returns the entity type to use for a sepecific semantic type
//     *
//     * @param semanticTypeName The name of the semantic type.
//     * @return The type of the entity that this semantic type needs.
//     */
//    Class<? extends AbstractEntityModel> getMappedModelTypes(String semanticTypeName);
//
//    /**
//     //     * Returns the entity type to use for a specified entity view.
//     //     *
//     //     * @param viewName The name of the entity view.
//     //     * @return The type of the entity that this entity view needs.
//     //     */
//     Class<? extends AbstractEntityModel> getViewEntityClass(String viewName);
//
//        /**
//     * Registers an entity type for a view.
//     *
//     * @param viewName The name of the entity view.
//     * @deprecated use registerViewModel(MvcData viewData,  Class<? extends AbstractEntityModel> entityClass) instead
//     */
//    @Deprecated
//    void registerViewEntityClass(String viewName, Class<? extends AbstractEntityModel> entityClass);
//
//*/
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
     * @param semanticTypeName The name of the semantic type.
     * @return The type of the entity that this semantic type needs.
     */
    Class<? extends AbstractEntityModel> getMappedModelTypes(String semanticTypeName);

    //Class<? extends AbstractEntityModel> getViewModelType(MvcData regionMvcData);
}

package com.sdl.webapp.common.api.mapping.semantic;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.views.AbstractModuleInitializer;
import com.sdl.webapp.common.api.model.EntityModel;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * <p>The semantic mapping registry contains information about the entity classes, gathered from the semantic mapping
 * annotations declared on the classes and the fields of the classes.</p>
 * @dxa.publicApi
 */
public interface SemanticMappingRegistry {

    /**
     * Gets the field semantics for the specified field.
     *
     * @param field The field.
     * @return A list of {@link FieldSemantics} objects for the field. Never returns <code>null</code>; if there are no
     * field semantics for the field, an empty collection is returned.
     */
    Set<FieldSemantics> getFieldSemantics(Field field);

    /**
     * Gets semantic information for the specified entity class. This information is gathered from the semantic entity
     * annotation(s) on the entity class and all of its superclasses.
     *
     * @param entityClass The entity class.
     * @return A list of {@link SemanticEntityInfo} objects containing semantic entity information for the entity class.
     * Never returns <code>null</code>; if there is no information for the class, an empty collection is returned.
     */
    Set<SemanticEntityInfo> getEntityInfo(Class<? extends EntityModel> entityClass);

    /**
     * Gets semantic information for the specified field. This information is gathered from the semantic property
     * annotation(s) on the field.
     *
     * @param field The field.
     * @return A list of {@link SemanticPropertyInfo} objects containing semantic property information for the field.
     * Never returns <code>null</code>; if there is no information for the field, an empty collection is returned.
     */
    Set<SemanticPropertyInfo> getPropertyInfo(Field field);

    /**
     * Registers the entity classes in the specified package and subpackages.
     *
     * @param basePackage The base package - this package and its subpackages are scanned for entity classes.
     * @deprecated since 2.0, use {@link AbstractModuleInitializer} for this
     */
    @Deprecated
    void registerEntities(String basePackage);

    /**
     * Registers the specified entity class.
     *
     * @param entityClass The entity class.
     */
    void registerEntity(Class<? extends EntityModel> entityClass);

    /**
     * Get entity class.
     *
     * @return entity class
     * @param entityName an entity name
     */
    Class<? extends EntityModel> getEntityClass(String entityName);


    /**
     * Get entity class.
     *
     * @return entity class
     * @param entityName an entity name
     * @param expectedClass an expected class or null if none
     */
    Class<? extends EntityModel> getEntityClassByFullyQualifiedName(String entityName, Class<? extends EntityModel> expectedClass) throws SemanticMappingException;

}

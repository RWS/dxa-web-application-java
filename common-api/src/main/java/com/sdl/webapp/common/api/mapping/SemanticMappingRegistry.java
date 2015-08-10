package com.sdl.webapp.common.api.mapping;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.model.Entity;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Semantic mapping registry.
 *
 * The semantic mapping registry contains information about the entity classes, gathered from the semantic mapping
 * annotations declared on the classes and the fields of the classes.
 */
public interface SemanticMappingRegistry {

    /**
     * Gets the field semantics for the specified field.
     *
     * @param field The field.
     * @return A list of {@code FieldSemantics} objects for the field. Never returns {@code null}; if there are no
     *      field semantics for the field, an empty collection is returned.
     */
    List<FieldSemantics> getFieldSemantics(Field field);

    /**
     * Gets semantic information for the specified entity class. This information is gathered from the semantic entity
     * annotation(s) on the entity class and all of its superclasses.
     *
     * @param entityClass The entity class.
     * @return A list of {@code SemanticEntityInfo} objects containing semantic entity information for the entity class.
     *      Never returns {@code null}; if there is no information for the class, an empty collection is returned.
     */
    List<SemanticEntityInfo> getEntityInfo(Class<? extends Entity> entityClass);

    /**
     * Gets semantic information for the specified field. This information is gathered from the semantic property
     * annotation(s) on the field.
     *
     * @param field The field.
     * @return A list of {@code SemanticPropertyInfo} objects containing semantic property information for the field.
     *      Never returns {@code null}; if there is no information for the field, an empty collection is returned.
     */
    List<SemanticPropertyInfo> getPropertyInfo(Field field);

    /**
     * Registers the entity classes in the specified package and subpackages.
     *
     * @param basePackage The base package - this package and its subpackages are scanned for entity classes.
     */
    public void registerEntities(String basePackage);

    /**
     * Registers the specified entity class.
     *
     * @param entityClass The entity class.
     */
    public void registerEntity(Class<? extends Entity> entityClass);
}

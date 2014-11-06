package com.sdl.webapp.common.api.mapping;

import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import java.util.Map;

/**
 * Semantic mapper.
 */
public interface SemanticMapper {

    /**
     * Creates an entity of the specified type and fills the fields of the entity by performing semantic mapping.
     *
     * @param entityClass The type of the entity to be created.
     * @param semanticFields The semantic fields to be used when performing semantic mapping.
     * @param fieldDataProvider A field data provider which provides the actual data for the fields when they are
     *                          mapped.
     * @param <T> The entity type.
     * @return An entity of the specified type, in which the fields are filled with data provided by the field data
     *      provider.
     * @throws SemanticMappingException When an error occurs.
     */
    <T extends AbstractEntity> T createEntity(Class<? extends T> entityClass,
                                              Map<FieldSemantics, SemanticField> semanticFields,
                                              SemanticFieldDataProvider fieldDataProvider)
            throws SemanticMappingException;
}

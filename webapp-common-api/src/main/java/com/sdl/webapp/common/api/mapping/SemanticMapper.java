package com.sdl.webapp.common.api.mapping;

import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.Entity;

import java.util.Map;

/**
 * TODO: Documentation.
 */
public interface SemanticMapper {

    Entity createEntity(Class<? extends Entity> entityClass, Map<FieldSemantics, SemanticField> semanticFields,
                        SemanticFieldDataProvider fieldDataProvider) throws SemanticMappingException;
}

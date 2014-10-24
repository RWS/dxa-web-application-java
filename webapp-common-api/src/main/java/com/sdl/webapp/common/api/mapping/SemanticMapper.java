package com.sdl.webapp.common.api.mapping;

import com.sdl.webapp.common.api.model.Entity;

/**
 * TODO: Documentation.
 */
public interface SemanticMapper {

    // TODO: er moet nog een callback bij om de field data te krijgen
    Entity createEntity(Class<? extends Entity> entityClass) throws SemanticMappingException;
}

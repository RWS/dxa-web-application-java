package com.sdl.webapp.common.api.mapping;

import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.Entity;

public interface SemanticMapper {

    Entity createEntity(Class<? extends Entity> entityClass, SemanticSchema schema,
                        SemanticFieldDataProvider fieldDataProvider) throws SemanticMappingException;
}

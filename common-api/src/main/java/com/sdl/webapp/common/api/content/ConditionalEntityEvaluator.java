package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.model.EntityModel;

public interface ConditionalEntityEvaluator {

    boolean IncludeEntity(EntityModel entity);
}

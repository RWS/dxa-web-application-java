package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.model.Entity;

public interface ConditionalEntityEvaluator {

    boolean IncludeEntity(Entity entity);
}

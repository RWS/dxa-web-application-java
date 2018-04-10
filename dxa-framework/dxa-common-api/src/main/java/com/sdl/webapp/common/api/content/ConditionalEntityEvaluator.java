package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.model.EntityModel;

/**
 * Conditional Entity Evaluator evaluates whether the entity should be included in a page.
 *
 * @dxa.publicApi
 */
@FunctionalInterface
public interface ConditionalEntityEvaluator {

    /**
     * Evaluates if the entity should be included.
     *
     * @param entity entity to evaluate
     * @return true if should be included, false otherwise
     */
    boolean includeEntity(EntityModel entity);
}

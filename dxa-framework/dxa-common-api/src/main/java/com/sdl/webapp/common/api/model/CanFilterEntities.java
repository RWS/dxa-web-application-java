package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;

import java.util.Collection;

/**
 * The implementor can filter {@link EntityModel} accepting a list of {@link ConditionalEntityEvaluator}.
 *
 * @dxa.publicApi
 */
public interface CanFilterEntities {

    /**
     * Default implementation does not filter anything.
     *
     * @param evaluators list of evaluators to evaluate entities against.
     */
    default void filterConditionalEntities(Collection<ConditionalEntityEvaluator> evaluators) {
    }

}

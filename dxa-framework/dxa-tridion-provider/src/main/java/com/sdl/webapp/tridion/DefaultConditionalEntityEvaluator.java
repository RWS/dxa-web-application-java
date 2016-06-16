package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.model.EntityModel;
import org.springframework.stereotype.Component;

/**
 * Default unused core implementation of {@link ConditionalEntityEvaluator}.
 * @deprecated since 1.5
 */
@Component
@Deprecated
public class DefaultConditionalEntityEvaluator implements ConditionalEntityEvaluator {

    /**
     * {@inheritDoc}
     * <p>Always returns true.</p>
     * @return always true
     */
    @Override
    public boolean includeEntity(EntityModel entity) {
        return true;
    }
}

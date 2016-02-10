package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.model.EntityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
/**
 * <p>DefaultConditionalEntityEvaluator class.</p>
 */
public class DefaultConditionalEntityEvaluator implements
        ConditionalEntityEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConditionalEntityEvaluator.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeEntity(EntityModel entity) {
        //TODO : currently, no implementation has been built, we just return true by default
        LOG.debug("Entering DefaultConditionalEntityEvaluator.IncludeEntity - returning true by default until real implementation");
        return true;
    }
}

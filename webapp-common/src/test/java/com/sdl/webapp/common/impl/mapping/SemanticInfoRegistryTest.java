package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.model.Entity;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class SemanticInfoRegistryTest {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticInfoRegistryTest.class);

    // TODO: Implement unit tests

    @Test
    public void test() throws SemanticMappingException {
        // TODO: Remove this, this is not a real test, just for debugging

        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntities("com.sdl.webapp.common.api.model");

        final Map<Class<? extends Entity>, Map<String, SemanticEntityInfo>> entityInfo = registry.getEntityInfo();
        LOG.debug("{}", entityInfo);
    }
}

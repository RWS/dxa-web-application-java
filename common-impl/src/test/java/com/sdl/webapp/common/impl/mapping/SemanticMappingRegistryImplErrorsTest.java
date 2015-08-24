package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.junit.Test;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * Unit tests for {@code SemanticMappingRegistry} that test error scenarios.
 */
public class SemanticMappingRegistryImplErrorsTest {

    @SemanticEntities({
            @SemanticEntity(entityName = "One", vocabulary = SDL_CORE, prefix = "x"),
            @SemanticEntity(entityName = "Two", vocabulary = SDL_CORE, prefix = "x")
    })
    public static class TestEntity1 extends AbstractEntity {
        @SemanticProperty("x:F1")
        private String field1;
    }

    public static class TestEntity2 extends AbstractEntity {
        @SemanticProperty("x:F1")
        private String field1;
    }


    @Test(expected = SemanticAnnotationException.class)
    public void testSemanticEntityAnnosWithTheSamePrefix() {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity1.class);
    }

    @Test(expected = SemanticAnnotationException.class)
    public void testSemanticPropertyAnnoWithUnknownPrefix() {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity2.class);
    }
}

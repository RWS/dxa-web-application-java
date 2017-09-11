package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG_VOCABULARY;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code SemanticMappingRegistry} that test if default mapping information (when not explicitly
 * specified via annotations) is registered correctly.
 */
public class SemanticMappingRegistryImplDefaultsTest {

    /**
     * Tests if default semantics are registered when an entity class has no semantic annotations at all.
     *
     * @throws NoSuchFieldException When a field does not exist (should not happen).
     */
    @Test
    public void testRegisterDefaults() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity1.class);

        final Set<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field1"));
        assertThat("Default FieldSemantics should be present for field1", field1, hasSize(1));
        assertThat(field1.iterator().next(), is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity1.class.getSimpleName(),
                "field1")));

        final Set<FieldSemantics> field2 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field2"));
        assertThat("Default FieldSemantics should be present for field2", field2, hasSize(1));
        assertThat(field2.iterator().next(), is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity1.class.getSimpleName(),
                "field2")));
    }

    /**
     * Tests if default semantics are registered when an entity class has semantic entity annotations that do not use
     * the default prefix. Also tests the order in which the field semantics are present; the default semantics must
     * be last.
     *
     * @throws NoSuchFieldException When a field does not exist (should not happen).
     */
    @Test
    public void testRegisterDefaultsWhenAnnotationsPresent() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity2.class);

        final Set<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity2.class.getDeclaredField("field1"));
        assertThat("Besides the specified semantics, there should be default semantics for field1", field1, hasSize(2));
        Iterator<FieldSemantics> iterator1 = field1.iterator();
        assertThat("The specified semantics must be first in the list", iterator1.next(),
                is(new FieldSemantics(SDL_CORE_VOCABULARY, "TE2", "F1")));
        assertThat("The default semantics must be second in the list", iterator1.next(),
                is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity2.class.getSimpleName(), "field1")));

    }

    /**
     * Tests that if you use annotations with the default prefix, these will override the default semantics.
     *
     * @throws NoSuchFieldException When a field does not exist (should not happen).
     */
    @Test
    public void testRegisterDefaultPrefix() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity3.class);

        final Set<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity3.class.getDeclaredField("field1"));
        assertThat("There should be only one FieldSemantics for field1", field1, hasSize(1));
        assertThat(field1.iterator().next(), is(new FieldSemantics(SCHEMA_ORG_VOCABULARY, "TE3", "F1")));
    }

    public static class TestEntity1 extends AbstractEntityModel {
        private String field1;
        private int field2;
    }

    @SemanticEntity(entityName = "TE2", vocabulary = SDL_CORE, prefix = "x")
    public static class TestEntity2 extends AbstractEntityModel {
        @SemanticProperty("x:F1")
        private String field1;

        @SemanticProperty("x:F2")
        private int field2;
    }

    @SemanticEntity(entityName = "TE3", vocabulary = SCHEMA_ORG)
    public static class TestEntity3 extends AbstractEntityModel {
        @SemanticProperty("F1")
        private String field1;
    }
}

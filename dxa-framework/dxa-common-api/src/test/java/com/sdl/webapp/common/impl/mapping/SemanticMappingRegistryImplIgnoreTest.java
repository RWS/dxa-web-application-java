package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link SemanticMappingRegistryImpl} that test if the {@link SemanticMappingIgnore} annotation on
 * entities and fields is handled correctly.
 */
public class SemanticMappingRegistryImplIgnoreTest {

    @Test
    public void testIgnoreOnEntity() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity1.class);

        final Set<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field1"));
        assertThat(field1, empty());

        final Set<FieldSemantics> field2 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field2"));
        assertThat(field2, empty());
    }

    @Test
    public void testIgnoreOnField() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity2.class);

        final Set<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity2.class.getDeclaredField("field1"));
        assertThat("field1 has a @SemanticMappingIgnore annotation, so there should not be semantics for field1",
                field1, empty());

        final Set<FieldSemantics> field2 = registry.getFieldSemantics(TestEntity2.class.getDeclaredField("field2"));
        assertThat("There should be semantics for field2", field2, hasSize(1));
        assertThat(field2.iterator().next(), is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity2.class.getSimpleName(),
                "field2")));

        final Set<FieldSemantics> field3 = registry.getFieldSemantics(TestEntity2.class.getDeclaredField("field3"));
        assertThat("field3 is static and should therefore be ignored", field3, empty());
    }

    @SemanticMappingIgnore
    public static class TestEntity1 extends AbstractEntityModel {
        private String field1;
        private int field2;
    }

    public static class TestEntity2 extends AbstractEntityModel {
        private static String field3;
        @SemanticMappingIgnore
        private String field1;
        private int field2;
    }
}

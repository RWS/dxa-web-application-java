package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.junit.Test;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code SemanticMappingRegistry} that test if the {@code @SemanticMappingIgnore} annotation on
 * entities and fields is handled correctly.
 */
public class SemanticMappingRegistryImplIgnoreTest {

    @SemanticMappingIgnore
    public static class TestEntity1 extends AbstractEntity {
        private String field1;
        private int field2;
    }

    public static class TestEntity2 extends AbstractEntity {
        @SemanticMappingIgnore
        private String field1;
        private int field2;
        private static String field3;
    }

    @Test
    public void testIgnoreOnEntity() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity1.class);

        final List<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field1"));
        assertThat(field1, empty());

        final List<FieldSemantics> field2 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field2"));
        assertThat(field2, empty());
    }

    @Test
    public void testIgnoreOnField() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity2.class);

        final List<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity2.class.getDeclaredField("field1"));
        assertThat("field1 has a @SemanticMappingIgnore annotation, so there should not be semantics for field1",
                field1, empty());

        final List<FieldSemantics> field2 = registry.getFieldSemantics(TestEntity2.class.getDeclaredField("field2"));
        assertThat("There should be semantics for field2", field2, hasSize(1));
        assertThat(field2.get(0), is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity2.class.getSimpleName(),
                "field2")));

        final List<FieldSemantics> field3 = registry.getFieldSemantics(TestEntity2.class.getDeclaredField("field3"));
        assertThat("field3 is static and should therefore be ignored", field3, empty());
    }
}

package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.junit.Test;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code SemanticMappingRegistry}.
 */
public class SemanticMappingRegistryImplTest {

    private static final String SDL_TEST = "http://www.sdl.com/web/schemas/test";

    private static final SemanticVocabulary SDL_TEST_VOCABULARY = new SemanticVocabulary(SDL_TEST);

    @SemanticEntities({
            @SemanticEntity(entityName = "TestOne", vocabulary = SDL_TEST, prefix = "t"),
            @SemanticEntity(entityName = "CoreOne", vocabulary = SDL_CORE, prefix = "c")
    })
    public static class TestEntity1 extends AbstractEntity {

        @SemanticProperties({
                @SemanticProperty(propertyName = "t:F1"),
                @SemanticProperty("c:one")
        })
        private String field1;

        @SemanticProperty("c:two")
        private int field2;
    }

    @Test
    public void testRegistration() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity1.class);

        final List<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field1"));
        assertThat("There should be semantics for the specified annotations and also default semantics for field1",
                field1, hasSize(3));
        assertThat("Semantics should be registered in the order that they were specified in the annotations",
                field1.get(0), is(new FieldSemantics(SDL_TEST_VOCABULARY, "TestOne", "F1")));
        assertThat("Semantics should be registered in the order that they were specified in the annotations",
                field1.get(1), is(new FieldSemantics(SDL_CORE_VOCABULARY, "CoreOne", "one")));
        assertThat("Default semantics should be last in the list", field1.get(2),
                is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity1.class.getSimpleName(),
                        "field1")));

        final List<FieldSemantics> field2 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field2"));
        assertThat("There should be semantics for TestOne and default semantics for field2", field2, hasSize(2));
        assertThat("Semantics for CoreOne.two should be first in the list",
                field2.get(0), is(new FieldSemantics(SDL_CORE_VOCABULARY, "CoreOne", "two")));
        assertThat("Default semantics should be last in the list", field2.get(1),
                is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity1.class.getSimpleName(),
                        "field2")));
    }
}

package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link SemanticMappingRegistryImpl}.
 */
public class SemanticMappingRegistryImplTest {

    private static final String SDL_TEST = "http://www.sdl.com/web/schemas/test";

    private static final SemanticVocabulary SDL_TEST_VOCABULARY = new SemanticVocabulary(SDL_TEST);

    @Test
    public void testRegistration() throws NoSuchFieldException {
        final SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity1.class);

        final Set<FieldSemantics> field1 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field1"));
        assertThat("There should be semantics for the specified annotations and also default semantics for field1",
                field1, hasSize(3));
        Iterator<FieldSemantics> iterator1 = field1.iterator();
        assertThat("Semantics should be registered in the order that they were specified in the annotations",
                iterator1.next(), is(new FieldSemantics(SDL_TEST_VOCABULARY, "TestOne", "F1")));
        assertThat("Semantics should be registered in the order that they were specified in the annotations",
                iterator1.next(), is(new FieldSemantics(SDL_CORE_VOCABULARY, "CoreOne", "one")));
        assertThat("Default semantics should be last in the list", iterator1.next(),
                is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity1.class.getSimpleName(),
                        "field1")));

        final Set<FieldSemantics> field2 = registry.getFieldSemantics(TestEntity1.class.getDeclaredField("field2"));
        assertThat("There should be semantics for TestOne and default semantics for field2", field2, hasSize(2));
        Iterator<FieldSemantics> iterator2 = field2.iterator();
        assertThat("Semantics for CoreOne.two should be first in the list",
                iterator2.next(), is(new FieldSemantics(SDL_CORE_VOCABULARY, "CoreOne", "two")));
        assertThat("Default semantics should be last in the list", iterator2.next(),
                is(new FieldSemantics(SDL_CORE_VOCABULARY, TestEntity1.class.getSimpleName(),
                        "field2")));
    }

    @Test
    public void shouldReturnEntityClassForFullName() throws SemanticMappingException {
        //given
        SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity1.class);

        //when
        Class<? extends EntityModel> sdlCore = registry.getEntityClassByFullyQualifiedName(SDL_CORE + ":TestEntity1", null);
        Class<? extends EntityModel> sdlTest = registry.getEntityClassByFullyQualifiedName(SDL_TEST + ":TestOne", null);
        Class<? extends EntityModel> sdlOne = registry.getEntityClassByFullyQualifiedName(SDL_CORE + ":CoreOne", null);
        Class<? extends EntityModel> random = registry.getEntityClassByFullyQualifiedName(SDL_CORE + ":Random", null);

        //then
        assertEquals(sdlCore, TestEntity1.class);
        assertEquals(sdlTest, TestEntity1.class);
        assertEquals(sdlOne, TestEntity1.class);
        assertNull(random);
    }


    @Test
    public void shouldChooseOneOfTwo_ForLegacy_IfManyClassesFound_AccordinglyToExpectedClass() throws SemanticMappingException {
        //given 
        SemanticMappingRegistryImpl registry = new SemanticMappingRegistryImpl();
        registry.registerEntity(TestEntity1.class);
        registry.registerEntity(TestEntity2.class);
        String fullyQualifiedName = SDL_TEST + ":TestEntity2";

        //when
        Class<? extends EntityModel> type = registry.getEntityClassByFullyQualifiedName(fullyQualifiedName, null);

        //when
        Class<? extends EntityModel> type1 = registry.getEntityClassByFullyQualifiedName(fullyQualifiedName, TestEntity1.class);

        //when
        Class<? extends EntityModel> type2 = registry.getEntityClassByFullyQualifiedName(fullyQualifiedName, TestEntity2.class);

        //then
        assertEquals(TestEntity2.class, type);
        assertEquals(TestEntity1.class, type1);
        assertEquals(TestEntity2.class, type2);
    }

    @SemanticEntities({
            @SemanticEntity(entityName = "TestOne", vocabulary = SDL_TEST, prefix = "t"),
            @SemanticEntity(entityName = "CoreOne", vocabulary = SDL_CORE, prefix = "c"),
            @SemanticEntity(entityName = "TestEntity2", vocabulary = SDL_TEST, prefix = "t2")
    })
    public static class TestEntity1 extends AbstractEntityModel {

        @SemanticProperties({
                @SemanticProperty(propertyName = "t:F1"),
                @SemanticProperty("c:one")
        })
        private String field1;

        @SemanticProperty("c:two")
        private int field2;
    }

    @SemanticEntities({
            @SemanticEntity(entityName = "TestEntity2", vocabulary = SDL_TEST)
    })
    public static class TestEntity2 extends AbstractEntityModel {

    }
}

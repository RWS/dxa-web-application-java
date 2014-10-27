package com.sdl.webapp.common.impl.mapping;

import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.annotations.*;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static com.sdl.webapp.common.impl.mapping.SemanticInfoRegistry.DEFAULT_VOCABULARY;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Unit tests for {@code SemanticInfoRegistry}.
 */
public class SemanticInfoRegistryTest {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticInfoRegistryTest.class);

    private static final String TEST_VOCABULARY = "http://www.sdl.com/web/schemas/test";

    public static class TestEntity1 extends AbstractEntity {
        private String field1;
    }

    @SemanticEntity("TestEntity")
    public static class TestEntity2 extends AbstractEntity {
        @SemanticProperty("TestField")
        private String field1;
    }

    @SemanticEntity(entityName = "TestEntity", vocabulary = TEST_VOCABULARY, prefix = "x")
    public static class TestEntity3 extends AbstractEntity {
        @SemanticProperty("x:Field1")
        private String field1;

        @SemanticProperty("Field2")
        private int field2;

        @SemanticProperty(propertyName = "x:")
        private long field3;
    }

    @SemanticEntities({
            @SemanticEntity(value = "TestEntityA", public_ = true),
            @SemanticEntity(entityName = "TestEntityB", vocabulary = TEST_VOCABULARY, prefix = "x")
    })
    public static class TestEntity4 extends AbstractEntity {
        @SemanticProperties({
                @SemanticProperty,
                @SemanticProperty("x:FieldOne")
        })
        private String field1;

        @SemanticMappingIgnore
        private int field2;
    }

    @SemanticEntities({
            @SemanticEntity("One"), // Two SemanticEntity annotations with the same prefix (the default prefix)
            @SemanticEntity("Two")  // will cause an error
    })
    public static class TestErrorEntity1 extends AbstractEntity {
    }

    @SemanticEntity(prefix = "x")
    public static class TestErrorEntity2 extends AbstractEntity {
        @SemanticProperty("y:Field1") // SemanticProperty with wrong prefix will cause an error
        private String field1;
    }

    @Test
    public void testRegisterEntity1() throws SemanticMappingException, NoSuchFieldException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity1.class);

        final ListMultimap<Class<? extends Entity>, SemanticEntityInfo> allEntityInfo = registry.getEntityInfo();
        assertThat(allEntityInfo.size(), is(1));
        assertTrue("Registry must contain info for test entity class", allEntityInfo.containsKey(TestEntity1.class));

        final List<SemanticEntityInfo> testEntityInfo = allEntityInfo.get(TestEntity1.class);
        assertThat(testEntityInfo.size(), is(1));

        final SemanticEntityInfo defaultPrefixEntityInfo = testEntityInfo.get(0);
        checkEntityInfo(TestEntity1.class.getSimpleName(), DEFAULT_VOCABULARY, false, defaultPrefixEntityInfo);

        final List<SemanticPropertyInfo> propertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(propertyInfo.size(), is(1));
        checkPropertyInfo("field1", TestEntity1.class.getDeclaredField("field1"), propertyInfo.get(0));
    }

    @Test
    public void testRegisterEntity2() throws SemanticMappingException, NoSuchFieldException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity2.class);

        final ListMultimap<Class<? extends Entity>, SemanticEntityInfo> allEntityInfo = registry.getEntityInfo();
        assertThat(allEntityInfo.size(), is(1));
        assertTrue("Registry must contain info for test entity class", allEntityInfo.containsKey(TestEntity2.class));

        final List<SemanticEntityInfo> testEntityInfo = allEntityInfo.get(TestEntity2.class);
        assertThat(testEntityInfo.size(), is(1));

        final SemanticEntityInfo defaultPrefixEntityInfo = testEntityInfo.get(0);
        checkEntityInfo("TestEntity", DEFAULT_VOCABULARY, false, defaultPrefixEntityInfo);

        final List<SemanticPropertyInfo> propertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(propertyInfo.size(), is(1));
        checkPropertyInfo("TestField", TestEntity2.class.getDeclaredField("field1"), propertyInfo.get(0));
    }

    @Test
    public void testRegisterEntity3() throws SemanticMappingException, NoSuchFieldException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity3.class);

        final ListMultimap<Class<? extends Entity>, SemanticEntityInfo> allEntityInfo = registry.getEntityInfo();
        assertThat(allEntityInfo.size(), is(2));
        assertTrue("Registry must contain info for test entity class", allEntityInfo.containsKey(TestEntity3.class));

        final List<SemanticEntityInfo> testEntityInfo = allEntityInfo.get(TestEntity3.class);
        assertThat(testEntityInfo.size(), is(2));

        SemanticEntityInfo xEntityInfo = null;
        SemanticEntityInfo defaultPrefixEntityInfo = null;
        for (SemanticEntityInfo entityInfo : testEntityInfo) {
            if (TEST_VOCABULARY.equals(entityInfo.getVocabulary())) {
                xEntityInfo = entityInfo;
            } else if (DEFAULT_VOCABULARY.equals(entityInfo.getVocabulary())) {
                defaultPrefixEntityInfo = entityInfo;
            }
        }

        assertNotNull("List should contain SemanticEntityInfo for test vocabulary", xEntityInfo);
        assertNotNull("List should contain SemanticEntityInfo for default vocabulary", defaultPrefixEntityInfo);

        checkEntityInfo("TestEntity", TEST_VOCABULARY, false, xEntityInfo);
        checkEntityInfo(TestEntity3.class.getSimpleName(), DEFAULT_VOCABULARY, false, defaultPrefixEntityInfo);

        final List<SemanticPropertyInfo> xPropertyInfo = xEntityInfo.getPropertyInfo();
        assertThat(xPropertyInfo.size(), is(2));
        checkPropertyInfoContains("Field1", TestEntity3.class.getDeclaredField("field1"), xPropertyInfo);
        checkPropertyInfoContains("field3", TestEntity3.class.getDeclaredField("field3"), xPropertyInfo);

        final List<SemanticPropertyInfo> defaultPrefixPropertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(defaultPrefixPropertyInfo.size(), is(3));
        checkPropertyInfoContains("field1", TestEntity3.class.getDeclaredField("field1"), defaultPrefixPropertyInfo);
        checkPropertyInfoContains("Field2", TestEntity3.class.getDeclaredField("field2"), defaultPrefixPropertyInfo);
        checkPropertyInfoContains("field3", TestEntity3.class.getDeclaredField("field3"), defaultPrefixPropertyInfo);
    }

    @Test
    public void testRegisterEntity4() throws SemanticMappingException, NoSuchFieldException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity4.class);

        final ListMultimap<Class<? extends Entity>, SemanticEntityInfo> allEntityInfo = registry.getEntityInfo();
        assertThat(allEntityInfo.size(), is(2));
        assertTrue("Registry must contain info for test entity class", allEntityInfo.containsKey(TestEntity4.class));

        final List<SemanticEntityInfo> testEntityInfo = allEntityInfo.get(TestEntity4.class);
        assertThat(testEntityInfo.size(), is(2));

        SemanticEntityInfo xEntityInfo = null;
        SemanticEntityInfo defaultPrefixEntityInfo = null;
        for (SemanticEntityInfo entityInfo : testEntityInfo) {
            if (TEST_VOCABULARY.equals(entityInfo.getVocabulary())) {
                xEntityInfo = entityInfo;
            } else if (DEFAULT_VOCABULARY.equals(entityInfo.getVocabulary())) {
                defaultPrefixEntityInfo = entityInfo;
            }
        }

        assertNotNull("List should contain SemanticEntityInfo for test vocabulary", xEntityInfo);
        assertNotNull("List should contain SemanticEntityInfo for default vocabulary", defaultPrefixEntityInfo);

        checkEntityInfo("TestEntityB", TEST_VOCABULARY, false, xEntityInfo);
        checkEntityInfo("TestEntityA", DEFAULT_VOCABULARY, true, defaultPrefixEntityInfo);

        final List<SemanticPropertyInfo> xPropertyInfo = xEntityInfo.getPropertyInfo();
        assertThat(xPropertyInfo.size(), is(1));
        checkPropertyInfo("FieldOne", TestEntity4.class.getDeclaredField("field1"), xPropertyInfo.get(0));

        final List<SemanticPropertyInfo> defaultPrefixPropertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(defaultPrefixPropertyInfo.size(), is(1));
        checkPropertyInfo("field1", TestEntity4.class.getDeclaredField("field1"), defaultPrefixPropertyInfo.get(0));
    }

    @Test(expected = SemanticMappingException.class)
    public void testRegisterErrorEntity1() throws SemanticMappingException {
        new SemanticInfoRegistry().registerEntity(TestErrorEntity1.class);
    }

    @Test(expected = SemanticMappingException.class)
    public void testRegisterErrorEntity2() throws SemanticMappingException {
        new SemanticInfoRegistry().registerEntity(TestErrorEntity2.class);
    }

    @Test
    public void testRegisterEntities() throws SemanticMappingException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntities("com.sdl.webapp.common.api.model.entity");

        LOG.debug("{}", registry.getEntityInfo());
    }

    private void checkEntityInfo(String expectedEntityName, String expectedVocabulary, boolean expectedPublic,
                                 SemanticEntityInfo actual) {
        assertThat(actual.getEntityName(), is(expectedEntityName));
        assertThat(actual.getVocabulary(), is(expectedVocabulary));
        assertThat(actual.isPublic(), is(expectedPublic));
    }

    private void checkPropertyInfo(String expectedPropertyName, Field expectedField, SemanticPropertyInfo actual) {
        assertThat(actual.getPropertyName(), is(expectedPropertyName));
        assertThat(actual.getField(), is(expectedField));
    }

    private void checkPropertyInfoContains(String expectedPropertyName, Field expectedField,
                                           Collection<SemanticPropertyInfo> actualCollection) {
        assertThat(actualCollection, hasItem(new SemanticPropertyInfo(expectedPropertyName, expectedField)));
    }
}

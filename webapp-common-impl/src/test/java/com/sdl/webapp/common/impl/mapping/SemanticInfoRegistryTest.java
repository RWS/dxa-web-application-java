package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.sdl.webapp.common.impl.mapping.SemanticInfoRegistry.DEFAULT_PREFIX;
import static com.sdl.webapp.common.impl.mapping.SemanticInfoRegistry.DEFAULT_VOCABULARY;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

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

        @SemanticProperty(ignoreMapping = true)
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

        final Map<Class<? extends Entity>, Map<String, SemanticEntityInfo>> allEntityInfo = registry.getEntityInfo();
        assertThat(allEntityInfo.size(), is(1));
        assertTrue("Registry must contain info for test entity class", allEntityInfo.containsKey(TestEntity1.class));

        final Map<String, SemanticEntityInfo> testEntityInfo = allEntityInfo.get(TestEntity1.class);
        assertThat(testEntityInfo.size(), is(1));
        assertThat(testEntityInfo, hasKey(DEFAULT_PREFIX));

        final SemanticEntityInfo defaultPrefixEntityInfo = testEntityInfo.get(DEFAULT_PREFIX);
        checkEntityInfo(TestEntity1.class.getSimpleName(), DEFAULT_VOCABULARY, DEFAULT_PREFIX, false, defaultPrefixEntityInfo);

        final List<SemanticPropertyInfo> propertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(propertyInfo.size(), is(1));
        checkPropertyInfo(DEFAULT_PREFIX, "field1", TestEntity1.class.getDeclaredField("field1"), propertyInfo.get(0));
    }

    @Test
    public void testRegisterEntity2() throws SemanticMappingException, NoSuchFieldException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity2.class);

        final Map<Class<? extends Entity>, Map<String, SemanticEntityInfo>> allEntityInfo = registry.getEntityInfo();
        assertThat(allEntityInfo.size(), is(1));
        assertTrue("Registry must contain info for test entity class", allEntityInfo.containsKey(TestEntity2.class));

        final Map<String, SemanticEntityInfo> testEntityInfo = allEntityInfo.get(TestEntity2.class);
        assertThat(testEntityInfo.size(), is(1));
        assertThat(testEntityInfo, hasKey(DEFAULT_PREFIX));

        final SemanticEntityInfo defaultPrefixEntityInfo = testEntityInfo.get(DEFAULT_PREFIX);
        checkEntityInfo("TestEntity", DEFAULT_VOCABULARY, DEFAULT_PREFIX, false, defaultPrefixEntityInfo);

        final List<SemanticPropertyInfo> propertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(propertyInfo.size(), is(1));
        checkPropertyInfo(DEFAULT_PREFIX, "TestField", TestEntity2.class.getDeclaredField("field1"), propertyInfo.get(0));
    }

    @Test
    public void testRegisterEntity3() throws SemanticMappingException, NoSuchFieldException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity3.class);

        final Map<Class<? extends Entity>, Map<String, SemanticEntityInfo>> allEntityInfo = registry.getEntityInfo();
        assertThat(allEntityInfo.size(), is(1));
        assertTrue("Registry must contain info for test entity class", allEntityInfo.containsKey(TestEntity3.class));

        final Map<String, SemanticEntityInfo> testEntityInfo = allEntityInfo.get(TestEntity3.class);
        assertThat(testEntityInfo.size(), is(2));
        assertThat(testEntityInfo, hasKey("x"));
        assertThat(testEntityInfo, hasKey(DEFAULT_PREFIX));

        final SemanticEntityInfo xEntityInfo = testEntityInfo.get("x");
        checkEntityInfo("TestEntity", TEST_VOCABULARY, "x", false, xEntityInfo);

        final List<SemanticPropertyInfo> xPropertyInfo = xEntityInfo.getPropertyInfo();
        assertThat(xPropertyInfo.size(), is(2));
        checkPropertyInfoContains("x", "Field1", TestEntity3.class.getDeclaredField("field1"), xPropertyInfo);
        checkPropertyInfoContains("x", "field3", TestEntity3.class.getDeclaredField("field3"), xPropertyInfo);

        final SemanticEntityInfo defaultPrefixEntityInfo = testEntityInfo.get(DEFAULT_PREFIX);
        checkEntityInfo(TestEntity3.class.getSimpleName(), DEFAULT_VOCABULARY, DEFAULT_PREFIX, false, defaultPrefixEntityInfo);

        final List<SemanticPropertyInfo> defaultPrefixPropertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(defaultPrefixPropertyInfo.size(), is(1));
        checkPropertyInfo(DEFAULT_PREFIX, "Field2", TestEntity3.class.getDeclaredField("field2"), defaultPrefixPropertyInfo.get(0));
    }

    @Test
    public void testRegisterEntity4() throws SemanticMappingException, NoSuchFieldException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity4.class);

        final Map<Class<? extends Entity>, Map<String, SemanticEntityInfo>> allEntityInfo = registry.getEntityInfo();
        assertThat(allEntityInfo.size(), is(1));
        assertTrue("Registry must contain info for test entity class", allEntityInfo.containsKey(TestEntity4.class));

        final Map<String, SemanticEntityInfo> testEntityInfo = allEntityInfo.get(TestEntity4.class);
        assertThat(testEntityInfo.size(), is(2));
        assertThat(testEntityInfo, hasKey(DEFAULT_PREFIX));
        assertThat(testEntityInfo, hasKey("x"));

        final SemanticEntityInfo defaultPrefixEntityInfo = testEntityInfo.get(DEFAULT_PREFIX);
        checkEntityInfo("TestEntityA", DEFAULT_VOCABULARY, DEFAULT_PREFIX, true, defaultPrefixEntityInfo);

        final List<SemanticPropertyInfo> defaultPrefixPropertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(defaultPrefixPropertyInfo.size(), is(1));
        checkPropertyInfo(DEFAULT_PREFIX, "field1", TestEntity4.class.getDeclaredField("field1"), defaultPrefixPropertyInfo.get(0));

        final SemanticEntityInfo xEntityInfo = testEntityInfo.get("x");
        checkEntityInfo("TestEntityB", TEST_VOCABULARY, "x", false, xEntityInfo);

        final List<SemanticPropertyInfo> xPropertyInfo = xEntityInfo.getPropertyInfo();
        assertThat(xPropertyInfo.size(), is(1));
        checkPropertyInfo("x", "FieldOne", TestEntity4.class.getDeclaredField("field1"), xPropertyInfo.get(0));
    }

    @Test(expected = SemanticMappingException.class)
    public void testRegisterErrorEntity1() throws SemanticMappingException {
        new SemanticInfoRegistry().registerEntity(TestErrorEntity1.class);
    }

    @Test(expected = SemanticMappingException.class)
    public void testRegisterErrorEntity2() throws SemanticMappingException {
        new SemanticInfoRegistry().registerEntity(TestErrorEntity2.class);
    }

    private void checkEntityInfo(String expectedEntityName, String expectedVocabulary, String expectedPrefix,
                                 boolean expectedPublic, SemanticEntityInfo actual) {
        assertThat(actual.getEntityName(), is(expectedEntityName));
        assertThat(actual.getVocabulary(), is(expectedVocabulary));
        assertThat(actual.getPrefix(), is(expectedPrefix));
        assertThat(actual.isPublic(), is(expectedPublic));
    }

    private void checkPropertyInfo(String expectedPrefix, String expectedPropertyName, Field expectedField,
                                   SemanticPropertyInfo actual) {
        assertThat(actual.getPrefix(), is(expectedPrefix));
        assertThat(actual.getPropertyName(), is(expectedPropertyName));
        assertThat(actual.getField(), is(expectedField));
    }

    private void checkPropertyInfoContains(String expectedPrefix, String expectedPropertyName, Field expectedField,
                                           Collection<SemanticPropertyInfo> actualCollection) {
        assertThat(actualCollection, hasItem(new SemanticPropertyInfo(expectedPrefix, expectedPropertyName, expectedField)));
    }
}

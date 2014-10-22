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

import java.util.List;
import java.util.Map;

import static com.sdl.webapp.common.impl.mapping.SemanticInfoRegistry.DEFAULT_PREFIX;
import static com.sdl.webapp.common.impl.mapping.SemanticInfoRegistry.DEFAULT_VOCABULARY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        assertTrue("Info for test entity class must be registered under the default prefix",
                testEntityInfo.containsKey(DEFAULT_PREFIX));

        final SemanticEntityInfo defaultPrefixEntityInfo = testEntityInfo.get(DEFAULT_PREFIX);
        assertThat(defaultPrefixEntityInfo.getEntityName(), is(TestEntity1.class.getSimpleName()));
        assertThat(defaultPrefixEntityInfo.getVocabulary(), is(DEFAULT_VOCABULARY));
        assertThat(defaultPrefixEntityInfo.getPrefix(), is(DEFAULT_PREFIX));
        assertFalse(defaultPrefixEntityInfo.isPublic());

        final List<SemanticPropertyInfo> propertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(propertyInfo.size(), is(1));

        SemanticPropertyInfo field1Info = propertyInfo.get(0);
        assertThat(field1Info.getPrefix(), is(DEFAULT_PREFIX));
        assertThat(field1Info.getPropertyName(), is("field1"));
        assertThat(field1Info.getField(), is(TestEntity1.class.getDeclaredField("field1")));
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
        assertTrue("Info for test entity class must be registered under the default prefix",
                testEntityInfo.containsKey(DEFAULT_PREFIX));

        final SemanticEntityInfo defaultPrefixEntityInfo = testEntityInfo.get(DEFAULT_PREFIX);
        assertThat(defaultPrefixEntityInfo.getEntityName(), is("TestEntity"));
        assertThat(defaultPrefixEntityInfo.getVocabulary(), is(DEFAULT_VOCABULARY));
        assertThat(defaultPrefixEntityInfo.getPrefix(), is(DEFAULT_PREFIX));
        assertFalse(defaultPrefixEntityInfo.isPublic());

        final List<SemanticPropertyInfo> propertyInfo = defaultPrefixEntityInfo.getPropertyInfo();
        assertThat(propertyInfo.size(), is(1));

        SemanticPropertyInfo field1Info = propertyInfo.get(0);
        assertThat(field1Info.getPrefix(), is(DEFAULT_PREFIX));
        assertThat(field1Info.getPropertyName(), is("TestField"));
        assertThat(field1Info.getField(), is(TestEntity2.class.getDeclaredField("field1")));
    }

    @Test
    public void testRegisterEntity3() throws SemanticMappingException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity3.class);

        // TODO
    }

    @Test
    public void testRegisterEntity4() throws SemanticMappingException {
        final SemanticInfoRegistry registry = new SemanticInfoRegistry();
        registry.registerEntity(TestEntity4.class);

        // TODO
    }

    @Test(expected = SemanticMappingException.class)
    public void testRegisterErrorEntity1() throws SemanticMappingException {
        new SemanticInfoRegistry().registerEntity(TestErrorEntity1.class);
    }

    @Test(expected = SemanticMappingException.class)
    public void testRegisterErrorEntity2() throws SemanticMappingException {
        new SemanticInfoRegistry().registerEntity(TestErrorEntity2.class);
    }
}

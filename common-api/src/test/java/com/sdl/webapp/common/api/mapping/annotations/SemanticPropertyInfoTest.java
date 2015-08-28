package com.sdl.webapp.common.api.mapping.annotations;

import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code SemanticPropertyInfo}.
 */
public class SemanticPropertyInfoTest {

    public static class TestEntity extends AbstractEntity {
        private String testField;
        private List<String> someList;
        private List<String> apples;
    }

    @Test
    public void testDefaults() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty("", "");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("testField"));
        assertThat(info.getPrefix(), is(SemanticEntityInfo.DEFAULT_PREFIX));
        assertThat(info.getPropertyName(), is("testField"));
    }

    @Test
    public void testPropertyNameWithoutPrefix() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty("PropName", "MyValue");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("testField"));
        assertThat(info.getPrefix(), is(SemanticEntityInfo.DEFAULT_PREFIX));
        assertThat("If property name is specified, it should be used (and not value or field name)",
                info.getPropertyName(), is("PropName"));
    }

    @Test
    public void testPropertyNameWithPrefix() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty("xy:PropName", "a:MyValue");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("testField"));
        assertThat(info.getPrefix(), is("xy"));
        assertThat("If property name is specified, it should be used (and not value or field name)",
                info.getPropertyName(), is("PropName"));
    }

    @Test
    public void testValueWithoutPrefix() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty("", "MyValue");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("testField"));
        assertThat(info.getPrefix(), is(SemanticEntityInfo.DEFAULT_PREFIX));
        assertThat("If property name is not specified but value is, then value should be used (and not field name)",
                info.getPropertyName(), is("MyValue"));
    }

    @Test
    public void testValueWithPrefix() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty("", "a:MyValue");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("testField"));
        assertThat(info.getPrefix(), is("a"));
        assertThat("If property name is not specified but value is, then value should be used (and not field name)",
                info.getPropertyName(), is("MyValue"));
    }

    @Test
    public void testPrefix1() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty(":PropName", "");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("testField"));
        assertThat(info.getPrefix(), is(""));
        assertThat(info.getPropertyName(), is("PropName"));
    }

    @Test
    public void testPrefix2() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty("PreFix:", "");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("testField"));
        assertThat(info.getPrefix(), is("PreFix"));
        assertThat(info.getPropertyName(), is("testField"));
    }

    @Test
    public void testPrefix3() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty(":", "");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("testField"));
        assertThat(info.getPrefix(), is(SemanticEntityInfo.DEFAULT_PREFIX));
        assertThat(info.getPropertyName(), is("testField"));
    }

    @Test
    public void testListDefaultName1() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty("", "");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("someList"));
        assertThat(info.getPrefix(), is(SemanticEntityInfo.DEFAULT_PREFIX));
        assertThat(info.getPropertyName(), is("someList"));
    }

    @Test
    public void testListDefaultName2() throws NoSuchFieldException {
        final SemanticProperty anno = mockSemanticProperty("", "");

        final SemanticPropertyInfo info = new SemanticPropertyInfo(anno, TestEntity.class.getDeclaredField("apples"));
        assertThat(info.getPrefix(), is(SemanticEntityInfo.DEFAULT_PREFIX));
        assertThat("The ending 's' should be removed from the name", info.getPropertyName(), is("apple"));
    }

    private SemanticProperty mockSemanticProperty(String propertyName, String value) {
        final SemanticProperty anno = mock(SemanticProperty.class);
        when(anno.propertyName()).thenReturn(propertyName);
        when(anno.value()).thenReturn(value);
        return anno;
    }
}

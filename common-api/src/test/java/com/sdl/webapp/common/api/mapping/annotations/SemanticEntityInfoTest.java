package com.sdl.webapp.common.api.mapping.annotations;

import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code SemanticEntityInfo}.
 */
public class SemanticEntityInfoTest {

    public static class TestEntity extends AbstractEntity {
    }

    @Test
    public void testDefaults() {
        final SemanticEntity anno = mockSemanticEntity("", "", "", "", false);

        final SemanticEntityInfo info = new SemanticEntityInfo(anno, TestEntity.class);
        assertThat(info.getVocabulary(), is(SemanticEntityInfo.DEFAULT_VOCABULARY));
        assertThat(info.getEntityName(), is(TestEntity.class.getSimpleName()));
        assertThat(info.getPrefix(), is(SemanticEntityInfo.DEFAULT_PREFIX));
        assertThat(info.isPublic(), is(false));
    }

    @Test
    public void testEntityName() {
        final SemanticEntity anno = mockSemanticEntity("SomeName", "TheValue", "", "", false);

        final SemanticEntityInfo info = new SemanticEntityInfo(anno, TestEntity.class);
        assertThat("If entity name is specified, it should be used (and not value or class name)",
                info.getEntityName(), is("SomeName"));
    }

    @Test
    public void testValue() {
        SemanticEntity anno = mockSemanticEntity("", "TheValue", "", "", false);

        final SemanticEntityInfo info = new SemanticEntityInfo(anno, TestEntity.class);
        assertThat("If entity name is not specified but value is, then value should be used (and not class name)",
                info.getEntityName(), is("TheValue"));
    }

    @Test
    public void testVocabulary() {
        final String vocab = "http://www.sdl.com/web/schemas/test";
        final SemanticEntity anno = mockSemanticEntity("", "", vocab, "", false);

        final SemanticEntityInfo info = new SemanticEntityInfo(anno, TestEntity.class);
        assertThat("If vocabulary is specified, it should be used (and not the default)",
                info.getVocabulary(), is(vocab));
    }

    @Test
    public void testPrefix() {
        final SemanticEntity anno = mockSemanticEntity("", "", "", "xy", false);

        final SemanticEntityInfo info = new SemanticEntityInfo(anno, TestEntity.class);
        assertThat("If prefix is specified, it should be used (and not the default)",
                info.getPrefix(), is("xy"));
    }

    private SemanticEntity mockSemanticEntity(String entityName, String value, String vocabulary, String prefix, boolean public_) {
        final SemanticEntity anno = mock(SemanticEntity.class);
        when(anno.entityName()).thenReturn(entityName);
        when(anno.value()).thenReturn(value);
        when(anno.vocabulary()).thenReturn(vocabulary);
        when(anno.prefix()).thenReturn(prefix);
        when(anno.public_()).thenReturn(public_);
        return anno;
    }
}

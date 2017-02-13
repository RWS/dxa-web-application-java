package com.sdl.webapp.tridion;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldPath;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl.ComponentEntity;
import com.sdl.webapp.tridion.fields.FieldConverterRegistry;
import com.sdl.webapp.tridion.fields.converters.ComponentLinkFieldConverter;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class SemanticFieldDataProviderImplTest {

    @Autowired
    private FieldConverterRegistry fieldConverterRegistry;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    private ComponentLinkFieldConverter fieldConverter;

    @Test
    public void shouldConstructDataProviderAndInjectFields() {
        //given 
        ComponentEntity semanticEntity = new ComponentEntity(null);

        //when
        SemanticFieldDataProvider provider = SemanticFieldDataProviderImpl.getFor(semanticEntity);

        //then
        FieldConverterRegistry fieldConverterRegistry = (FieldConverterRegistry) ReflectionTestUtils.getField(provider, "fieldConverterRegistry");
        ModelBuilderPipeline modelBuilderPipeline = (ModelBuilderPipeline) ReflectionTestUtils.getField(provider, "builder");
        ComponentEntity actualEntity = (ComponentEntity) ReflectionTestUtils.getField(provider, "semanticEntity");

        assertSame(this.fieldConverterRegistry, fieldConverterRegistry);
        assertSame(this.modelBuilderPipeline, modelBuilderPipeline);
        assertSame(semanticEntity, actualEntity);
    }

    @Test
    public void shouldReturnRightFieldsForPageEntity() throws FieldConverterException {
        //given 
        Page page = mock(Page.class);
        Map<String, Field> metadata = new HashMap<>();
        when(page.getMetadata()).thenReturn(metadata);

        //when
        SemanticFieldDataProviderImpl.PageEntity pageEntity = new SemanticFieldDataProviderImpl.PageEntity(page);
        pageEntity.injectDataProvider((SemanticFieldDataProviderImpl) SemanticFieldDataProviderImpl.getFor(pageEntity));
        pageEntity.createLink(Integer.class);

        //then
        assertSame(metadata, pageEntity.getFields());
        assertSame(metadata, pageEntity.getFields(new FieldPath("Metadata/Test")));
        assertTrue(pageEntity.getFields(new FieldPath("Whatever/Test")).size() == 0);
        verify(fieldConverter).createPageLink(eq(page), eq(Integer.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnRightFieldsForKeywordEntity() throws SemanticMappingException {
        //given
        Keyword keyword = mock(Keyword.class);
        Map<String, Field> metadata = new HashMap<>();
        when(keyword.getMetadata()).thenReturn(metadata);

        //when
        SemanticFieldDataProviderImpl.KeywordEntity keywordEntity = new SemanticFieldDataProviderImpl.KeywordEntity(keyword);
        keywordEntity.injectDataProvider((SemanticFieldDataProviderImpl) SemanticFieldDataProviderImpl.getFor(keywordEntity));

        //then
        assertSame(metadata, keywordEntity.getFields());
        assertSame(metadata, keywordEntity.getFields(new FieldPath("Metadata/Test")));
        assertTrue(keywordEntity.getFields(new FieldPath("Whatever/Test")).size() == 0);

        keywordEntity.createLink(Integer.class);
    }

    @Test
    public void shouldReturnRightFieldsForComponentEntity() throws SemanticMappingException {
        //given
        Component component = mock(Component.class);
        Map<String, Field> metadata = new HashMap<>();
        Map<String, Field> content = new HashMap<>();
        when(component.getMetadata()).thenReturn(metadata);
        when(component.getContent()).thenReturn(content);

        //when
        SemanticFieldDataProviderImpl.ComponentEntity componentEntity = new SemanticFieldDataProviderImpl.ComponentEntity(component);
        componentEntity.injectDataProvider((SemanticFieldDataProviderImpl) SemanticFieldDataProviderImpl.getFor(componentEntity));
        componentEntity.createLink(Integer.class);

        //then
        assertSame(content, componentEntity.getFields());
        assertSame(metadata, componentEntity.getFields(new FieldPath("Metadata/Test")));
        assertSame(content, componentEntity.getFields(new FieldPath("Whatever/Test")));
        verify(fieldConverter).createComponentLink(eq(component), eq(Integer.class), same(modelBuilderPipeline));
    }

    @Configuration
    @Profile("test")
    public static class SpringConfigurationContext {

        @Bean
        public ComponentLinkFieldConverter fieldConverter() {
            ComponentLinkFieldConverter mock = mock(ComponentLinkFieldConverter.class);
            when(mock.supportedFieldTypes()).thenReturn(new FieldType[]{FieldType.COMPONENTLINK});
            return mock;
        }

        @Bean
        public FieldConverterRegistry fieldConverterRegistry() {
            return new FieldConverterRegistry(Lists.newArrayList(fieldConverter()));
        }

        @Bean
        public ModelBuilderPipeline modelBuilderPipeline() {
            return mock(ModelBuilderPipeline.class);
        }

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }
    }
}
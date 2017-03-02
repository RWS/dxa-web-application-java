package com.sdl.webapp.tridion.fields.converters;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.KeywordModel;
import com.sdl.webapp.common.api.model.entity.Tag;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.FieldConverterRegistry;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.FieldSetImpl;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class KeywordFieldConverterTest {

    @Autowired
    private SemanticMapper semanticMapper;

    @Autowired
    private SemanticFieldDataProviderImpl semanticFieldDataProviderImpl;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    private WebRequestContext webRequestContext;

    private Map<FieldSemantics, SemanticField> semanticFields = new HashMap<>();

    private Map<Long, SemanticSchema> schemas = new HashMap<>();

    private KeywordFieldConverter converter;

    @Before
    public void init() throws SemanticMappingException {
        converter = new KeywordFieldConverter();

        doReturn(new KeywordModel()).when(semanticMapper).createEntity(eq(KeywordModel.class),
                anyMapOf(FieldSemantics.class, SemanticField.class),
                any(SemanticFieldDataProvider.class));

        Localization localization = mock(Localization.class);

        doReturn(localization).when(webRequestContext).getLocalization();

        schemas.put(8L, new SemanticSchema(8L, "test", Collections.emptySet(), semanticFields));
        doReturn(schemas).when(localization).getSemanticSchemas();
    }

    @Test
    public void shouldConvertKeywordToTag() throws FieldConverterException {
        //given 
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("tcm:1-1", "tcm:1-2", "title", null, null),
                mockKeyword(null, "tcm:1-3", null, "desc", "key"))).when(baseField).getKeywordValues();

        SemanticField semanticField = mockSemanticField(true);

        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, Tag.class);

        //when
        List<?> fieldValues = (List<?>) converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        assertEquals(2, fieldValues.size());
        Tag tag = (Tag) fieldValues.get(0);
        assertEquals("title", tag.getDisplayText());
        assertEquals("1", tag.getKey());
        assertEquals("2", tag.getTagCategory());

        tag = (Tag) fieldValues.get(1);
        assertEquals("desc", tag.getDisplayText());
        assertEquals("key", tag.getKey());
        assertEquals("3", tag.getTagCategory());
    }

    @Test
    public void shouldConvertKeywordToBoolean() throws FieldConverterException {
        //given 
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword(null, null, "false", null, "true"),
                mockKeyword(null, null, "true", null, null))).when(baseField).getKeywordValues();

        SemanticField semanticField = mockSemanticField(true);

        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, Boolean.class);

        //when
        List<?> fieldValues = (List<?>) converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        assertEquals(2, fieldValues.size());
        assertTrue((Boolean) fieldValues.get(0));
        assertTrue((Boolean) fieldValues.get(1));
    }

    @Test
    public void shouldConvertKeywordToStrings() throws FieldConverterException {
        //given
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword(null, null, "title", null, null),
                mockKeyword(null, null, null, "desc", null))).when(baseField).getKeywordValues();

        SemanticField semanticField = mockSemanticField(true);

        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, String.class);

        //when
        List<?> fieldValues = (List<?>) converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        assertEquals(2, fieldValues.size());
        assertEquals("title", fieldValues.get(0));
        assertEquals("desc", fieldValues.get(1));
    }

    @Test
    public void shouldConvertDd4tKeywordToDxaKeywordWhenMetadataIsNull() throws FieldConverterException {
        //given
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("tcm:0-1-2", "tcm:0-2-3", "title", "desc", "key"),
                mockKeyword("tcm:0-3-4", "tcm:0-4-5", "title2", "desc2", "key2", Collections.emptyMap()),
                mockKeyword("tcm:0-3-4", "tcm:0-4-5", "title2", "desc2", "key2", getExtensionData(Collections.emptyMap()))))
                .when(baseField).getKeywordValues();

        SemanticField semanticField = mockSemanticField(true);

        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, KeywordModel.class);

        //when
        List<?> fieldValues = (List<?>) converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        assertEquals(3, fieldValues.size());
        KeywordModel model = (KeywordModel) fieldValues.get(0);
        assertEquals("1", model.getId());
        assertEquals("key", model.getKey());
        assertEquals("2", model.getTaxonomyId());
        assertEquals("desc", model.getDescription());
        assertEquals("title", model.getTitle());

        model = (KeywordModel) fieldValues.get(1);
        assertEquals("3", model.getId());
        assertEquals("key2", model.getKey());
        assertEquals("4", model.getTaxonomyId());
        assertEquals("desc2", model.getDescription());
        assertEquals("title2", model.getTitle());

        model = (KeywordModel) fieldValues.get(2);
        assertEquals("3", model.getId());
        assertEquals("key2", model.getKey());
        assertEquals("4", model.getTaxonomyId());
        assertEquals("desc2", model.getDescription());
        assertEquals("title2", model.getTitle());
    }

    @Test
    public void shouldConvertDd4tKeywordWithModelMappingWhenMetadataExists() throws SemanticMappingException {
        //given
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("tcm:0-1-2", "tcm:0-2-3", "title", "desc", "key", getExtensionData(getMetadataSchemaId()))))
                .when(baseField).getKeywordValues();

        SemanticField semanticField = mockSemanticField(false);

        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, KeywordModel.class);

        //when
        Object fieldValue = converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        verify(semanticMapper).createEntity(eq(KeywordModel.class), eq(semanticFields), argThat(new BaseMatcher<SemanticFieldDataProviderImpl>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Should create a KeywordEntity data provider");
            }

            @Override
            public boolean matches(Object item) {
                return ReflectionTestUtils.getField(item, "semanticEntity") instanceof SemanticFieldDataProviderImpl.KeywordEntity;
            }
        }));
        verifyNoMoreInteractions(semanticMapper);

        KeywordModel model = (KeywordModel) fieldValue;
        assertEquals("1", model.getId());
        assertEquals("key", model.getKey());
        assertEquals("2", model.getTaxonomyId());
        assertEquals("desc", model.getDescription());
        assertEquals("title", model.getTitle());
    }

    @Test(expected = FieldConverterException.class)
    public void shouldThrowExceptionWhenMetadataExistsButSchemaIsNotLoaded() throws SemanticMappingException {
        //given
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("tcm:0-1-2", "tcm:0-2-3", "title", "desc", "key", getExtensionData(getMetadataSchemaId("tcm:10-20-30")))))
                .when(baseField).getKeywordValues();

        SemanticField semanticField = mockSemanticField(false);

        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, KeywordModel.class);

        //when
        converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        verifyZeroInteractions(semanticField);
    }

    @Test
    public void shouldSupportKeywordFieldType() {
        //given
        FieldType[] types = converter.supportedFieldTypes();

        //when
        boolean contains = Arrays.asList(types).contains(FieldType.KEYWORD);

        //then
        assertTrue(contains);
    }

    @Test(expected = UnsupportedTargetTypeException.class)
    public void shouldThrowExceptionWhenTypeNotSupported() throws FieldConverterException {
        //given
        BaseField baseField = mock(BaseField.class);

        SemanticField semanticField = mockSemanticField(false);

        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, Integer.class);

        //when
        converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        //exception
    }

    @Test
    public void shouldDeriveTargetTypeFromCollection() throws FieldConverterException {
        //given
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("tcm:0-1-2", "tcm:0-2-3", "title", "desc", "key", getExtensionData(getMetadataSchemaId()))))
                .when(baseField).getKeywordValues();

        TypeDescriptor typeDescriptor = mock(TypeDescriptor.class);
        when(typeDescriptor.isCollection()).thenReturn(true);
        TypeDescriptor innerType = mockTypeDescriptor(false, String.class);
        when(typeDescriptor.getElementTypeDescriptor()).thenReturn(innerType);

        SemanticField semanticField = mockSemanticField(false);


        //when
        Object fieldValue = converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        assertEquals("desc", fieldValue);
    }

    @Test
    public void shouldReturnNullIfKeywordEmpty() throws FieldConverterException {
        //given
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList()).when(baseField).getKeywordValues();

        TypeDescriptor typeDescriptor = mock(TypeDescriptor.class);
        when(typeDescriptor.isCollection()).thenReturn(true);
        TypeDescriptor innerType = mockTypeDescriptor(false, String.class);
        when(typeDescriptor.getElementTypeDescriptor()).thenReturn(innerType);

        SemanticField semanticField = mockSemanticField(false);

        //when
        Object fieldValue = converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        assertNull(fieldValue);
    }

    @Test(expected = FieldConverterException.class)
    public void shouldRaiseAnExceptionWhenSemanticMappingFailed() throws SemanticMappingException {
        //given
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("tcm:0-1-2", "tcm:0-2-3", "title", "desc", "key", getExtensionData(getMetadataSchemaId()))))
                .when(baseField).getKeywordValues();

        SemanticField semanticField = mockSemanticField(false);
        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, KeywordModel.class);

        doThrow(SemanticMappingException.class).when(semanticMapper).createEntity(eq(KeywordModel.class),
                anyMapOf(FieldSemantics.class, SemanticField.class),
                any(SemanticFieldDataProvider.class));

        //when
        Object fieldValue = converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        verify(semanticMapper).createEntity(eq(KeywordModel.class),
                anyMapOf(FieldSemantics.class, SemanticField.class),
                any(SemanticFieldDataProvider.class));
    }

    @Test
    public void shouldMapKeywordModelSubClasses() throws SemanticMappingException {
        //given 
        SemanticField semanticField = mockSemanticField(false);
        TypeDescriptor typeDescriptor = mockTypeDescriptor(false, KeywordModelSubclass.class);

        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("tcm:0-1-2", "tcm:0-2-3", "title", "desc", "key", getExtensionData(getMetadataSchemaId()))))
                .when(baseField).getKeywordValues();

        doReturn(new KeywordModelSubclass()).when(semanticMapper).createEntity(eq(KeywordModelSubclass.class),
                anyMapOf(FieldSemantics.class, SemanticField.class),
                any(SemanticFieldDataProvider.class));

        //when
        converter.getFieldValue(semanticField, baseField, typeDescriptor, semanticFieldDataProviderImpl, modelBuilderPipeline);

        //then
        verify(semanticMapper).createEntity(eq(KeywordModelSubclass.class),
                anyMapOf(FieldSemantics.class, SemanticField.class),
                any(SemanticFieldDataProvider.class));
    }

    @NotNull
    private HashMap<String, FieldSet> getExtensionData(Map<String, Field> metadataSchemaId) {
        return new HashMap<String, FieldSet>() {{
            FieldSetImpl set = new FieldSetImpl();
            set.setContent(metadataSchemaId);
            put("DXA", set);
        }};
    }

    private Map<String, Field> getMetadataSchemaId(String schemaId) {
        return new HashMap<String, Field>() {{
            Field field = new BaseField() {
                @Override
                public List<Object> getValues() {
                    return Lists.newArrayList(schemaId);
                }
            };
            put("MetadataSchemaId", field);
        }};
    }

    private Map<String, Field> getMetadataSchemaId() {
        return getMetadataSchemaId("tcm:9-8-7");
    }

    private SemanticField mockSemanticField(boolean multiValue) {
        return new SemanticField("mock", "/path", multiValue, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    private TypeDescriptor mockTypeDescriptor(boolean isCollection, Class clazz) {
        TypeDescriptor mock = mock(TypeDescriptor.class);
        when(mock.isCollection()).thenReturn(isCollection);
        when(mock.getObjectType()).thenReturn(clazz);
        return mock;
    }

    private Keyword mockKeyword(String id, String taxonomyId, String title, String description, String key) {
        return mockKeyword(id, taxonomyId, title, description, key, null);
    }

    private Keyword mockKeyword(String id, String taxonomyId, String title, String description, String key, Map<String, FieldSet> extensionData) {
        Keyword keyword = mock(Keyword.class);
        doReturn(id).when(keyword).getId();
        doReturn(taxonomyId).when(keyword).getTaxonomyId();
        doReturn(title).when(keyword).getTitle();
        doReturn(description).when(keyword).getDescription();
        doReturn(key).when(keyword).getKey();
        doReturn(extensionData).when(keyword).getExtensionData();
        return keyword;
    }

    private static class KeywordModelSubclass extends KeywordModel {

    }

    @Configuration
    @Profile("test")
    public static class SpringConfigurationContext {

        @Bean
        public FieldConverterRegistry fieldConverterRegistry() {
            return mock(FieldConverterRegistry.class);
        }

        @Bean
        public ModelBuilderPipeline modelBuilderPipeline() {
            return mock(ModelBuilderPipeline.class);
        }

        @Bean
        public SemanticMapper semanticMapper() {
            return mock(SemanticMapper.class);
        }

        @Bean
        public SemanticFieldDataProviderImpl semanticFieldDataProviderImpl() {
            return mock(SemanticFieldDataProviderImpl.class);
        }

        @Bean
        public WebRequestContext webRequestContext() {
            return mock(WebRequestContext.class);
        }

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }
    }
}
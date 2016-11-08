package com.sdl.webapp.tridion.fields.converters;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.model.KeywordModel;
import com.sdl.webapp.common.api.model.entity.Tag;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.FieldSetImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class KeywordFieldConverterTest {

    private KeywordFieldConverter converter = new KeywordFieldConverter();

    @Test
    public void shouldConvertKeywordToTag() throws FieldConverterException {
        //given 
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("id", "cat", "title", null, null),
                mockKeyword(null, "cat", null, "desc", "key"))).when(baseField).getKeywordValues();

        //when
        List<?> fieldValues = converter.getFieldValues(baseField, Tag.class, null);

        //then
        assertEquals(2, fieldValues.size());
        Tag tag = (Tag) fieldValues.get(0);
        assertEquals("title", tag.getDisplayText());
        assertEquals("id", tag.getKey());
        assertEquals("cat", tag.getTagCategory());

        tag = (Tag) fieldValues.get(1);
        assertEquals("desc", tag.getDisplayText());
        assertEquals("key", tag.getKey());
        assertEquals("cat", tag.getTagCategory());
    }

    @Test
    public void shouldConvertKeywordToBoolean() throws FieldConverterException {
        //given 
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword(null, null, "false", null, "true"),
                mockKeyword(null, null, "true", null, null))).when(baseField).getKeywordValues();

        //when
        List<?> fieldValues = converter.getFieldValues(baseField, Boolean.class, null);

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

        //when
        List<?> fieldValues = converter.getFieldValues(baseField, String.class, null);

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

        //when
        List<?> fieldValues = converter.getFieldValues(baseField, KeywordModel.class, null);

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

    @Test(expected = UnsupportedOperationException.class)
    public void shouldConvertDd4tKeywordWithModelMappingWhenMetadataExists() throws FieldConverterException {
        //given
        BaseField baseField = mock(BaseField.class);
        doReturn(Lists.newArrayList(
                mockKeyword("tcm:0-1-2", "tcm:0-2-3", "title", "desc", "key", getExtensionData(getMetadataSchemaId()))))
                .when(baseField).getKeywordValues();

        //when
        List<?> fieldValues = converter.getFieldValues(baseField, KeywordModel.class, null);

        //then
        assertEquals(1, fieldValues.size());
        KeywordModel model = (KeywordModel) fieldValues.get(0);
        assertEquals("1", model.getId());
        assertEquals("key", model.getKey());
        assertEquals("2", model.getTaxonomyId());
        assertEquals("desc", model.getDescription());
        assertEquals("title", model.getTitle());
    }

    @NotNull
    private HashMap<String, FieldSet> getExtensionData(Map<String, Field> metadataSchemaId) {
        return new HashMap<String, FieldSet>() {{
            FieldSetImpl set = new FieldSetImpl();
            set.setContent(metadataSchemaId);
            put("DXA", set);
        }};
    }

    private Map<String, Field> getMetadataSchemaId() {
        return new HashMap<String, Field>() {{
            Field field = new BaseField() {
                @Override
                public List<Object> getValues() {
                    return Lists.newArrayList("tcm:9-8-7");
                }
            };
            put("MetadataSchemaId", field);
        }};
    }

    @Test(expected = UnsupportedTargetTypeException.class)
    public void shouldThrowExceptionWhenTypeNotSupported() throws FieldConverterException {
        //given
        BaseField baseField = mock(BaseField.class);

        //when
        converter.getFieldValues(baseField, List.class, null);
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
}
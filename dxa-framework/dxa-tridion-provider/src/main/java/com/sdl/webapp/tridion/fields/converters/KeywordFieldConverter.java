package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.KeywordModel;
import com.sdl.webapp.common.api.model.entity.Tag;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Boolean.parseBoolean;

/**
 * Converts a DD4T {@linkplain FieldType#KEYWORD field type KEYWORD} into a DXA {@link Tag}, Java {@link Boolean} or {@link String}
 * depending on a target type.
 *
 * @see Tag
 * @see FieldType
 * @see AbstractFieldConverter
 */
@Component
@Slf4j
public class KeywordFieldConverter implements FieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.KEYWORD};

    private final SemanticMapper semanticMapper;

    @Autowired
    public KeywordFieldConverter(SemanticMapper semanticMapper) {
        this.semanticMapper = semanticMapper;
    }

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException {
        Class<?> targetClass = targetType.isCollection() ? targetType.getElementTypeDescriptor().getObjectType() : targetType.getObjectType();
        Converter<?> converter = getConverter(targetClass, semanticField, semanticFieldDataProvider);

        final List<Keyword> keywords = field.getKeywordValues();
        if (semanticField.isMultiValue()) {
            List<Object> list = new ArrayList<>();
            for (Keyword keyword : keywords) {
                list.add(converter.convert(keyword));
            }
            return list;
        } else {
            return keywords.isEmpty() ? null : converter.convert(keywords.get(0));
        }
    }

    private Converter<?> getConverter(Class<?> targetClass,
                                      SemanticField semanticField,
                                      SemanticFieldDataProvider semanticFieldDataProvider) throws UnsupportedTargetTypeException {
        Converter<?> converter;
        if (Tag.class.equals(targetClass)) {
            converter = new TagConverter();
        } else if (Boolean.class.isAssignableFrom(targetClass)) {
            converter = new BooleanConverter();
        } else if (String.class.isAssignableFrom(targetClass)) {
            converter = new StringConverter();
        } else if (KeywordModel.class.isAssignableFrom(targetClass)) {
            //typecast is safe which is guaranteed by if condition
            //noinspection unchecked
            converter = new KeywordConverter(semanticMapper, semanticField, semanticFieldDataProvider,
                    (Class<? extends KeywordModel>) targetClass);
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
        return converter;
    }

    @FunctionalInterface
    private interface Converter<T> {

        T convert(Keyword keyword) throws FieldConverterException;

        default String getKeywordDisplayText(Keyword keyword) {
            return isNullOrEmpty(keyword.getDescription()) ? keyword.getTitle() : keyword.getDescription();
        }

        default String getKeywordKey(Keyword keyword) {
            return isNullOrEmpty(keyword.getKey()) ? keyword.getId() : keyword.getKey();
        }
    }

    private static class TagConverter implements Converter<Tag> {

        @Override
        public Tag convert(Keyword keyword) {
            final Tag tag = new Tag();
            tag.setDisplayText(getKeywordDisplayText(keyword));
            tag.setKey(getKeywordKey(keyword));
            tag.setTagCategory(keyword.getTaxonomyId());
            return tag;
        }
    }

    private static class StringConverter implements Converter<String> {

        @Override
        public String convert(Keyword keyword) {
            return getKeywordDisplayText(keyword);
        }
    }

    private static class BooleanConverter implements Converter<Boolean> {

        @Override
        public Boolean convert(Keyword keyword) {
            final String key = keyword.getKey();
            final String title = keyword.getTitle();

            return parseBoolean(isNullOrEmpty(key) ? title : key);
        }
    }

    private static class KeywordConverter implements Converter<KeywordModel> {

        private final SemanticMapper semanticMapper;

        private SemanticField semanticField;

        private SemanticFieldDataProvider semanticFieldDataProvider;

        private Class<? extends KeywordModel> targetClass;

        KeywordConverter(SemanticMapper semanticMapper, SemanticField semanticField,
                         SemanticFieldDataProvider semanticFieldDataProvider, Class<? extends KeywordModel> targetClass) {
            this.semanticMapper = semanticMapper;
            this.semanticField = semanticField;
            this.semanticFieldDataProvider = semanticFieldDataProvider;
            this.targetClass = targetClass;
        }

        private String getMetadataSchemaId(Keyword keyword) {
            if (keyword.getExtensionData() == null ||
                    !keyword.getExtensionData().containsKey("DXA") ||
                    !keyword.getExtensionData().get("DXA").getContent().containsKey("MetadataSchemaId")) {
                return null;
            }

            return (String) keyword.getExtensionData().get("DXA").getContent().get("MetadataSchemaId").getValues().get(0);
        }

        @Override
        public KeywordModel convert(Keyword keyword) throws FieldConverterException {
            KeywordModel keywordModel;
            if (isNullOrEmpty(getMetadataSchemaId(keyword))) {
                keywordModel = new KeywordModel();
            } else {
                try {
                    keywordModel = semanticMapper.createEntity(this.targetClass, semanticField.getEmbeddedFields(), semanticFieldDataProvider);
                } catch (SemanticMappingException e) {
                    log.error("Failed to do a semantic mapping for keyword {}", keyword, e);
                    throw new FieldConverterException("Failed to do a semantic mapping for keyword", e);
                }
            }

            keywordModel.setId(String.valueOf(TcmUtils.getItemId(keyword.getId())));
            keywordModel.setTitle(keyword.getTitle());
            keywordModel.setDescription(keyword.getDescription());
            keywordModel.setKey(keyword.getKey());
            keywordModel.setTaxonomyId(String.valueOf(TcmUtils.getItemId(keyword.getTaxonomyId())));

            return keywordModel;
        }
    }
}

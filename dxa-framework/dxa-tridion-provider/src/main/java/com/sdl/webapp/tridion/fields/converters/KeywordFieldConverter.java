package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.model.KeywordModel;
import com.sdl.webapp.common.api.model.entity.Tag;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.BaseField;
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
public class KeywordFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.KEYWORD};

    private static final Converter<Tag> TAG_CONVERTER = new Converter<Tag>() {
        @Override
        public Tag convert(Keyword keyword) {
            final Tag tag = new Tag();
            tag.setDisplayText(getKeywordDisplayText(keyword));
            tag.setKey(getKeywordKey(keyword));
            tag.setTagCategory(keyword.getTaxonomyId());
            return tag;
        }
    };

    private static final Converter<String> STRING_CONVERTER = new Converter<String>() {
        @Override
        public String convert(Keyword keyword) {
            return getKeywordDisplayText(keyword);
        }
    };

    private static final Converter<Boolean> BOOLEAN_CONVERTER = new Converter<Boolean>() {
        @Override
        public Boolean convert(Keyword keyword) {
            final String key = keyword.getKey();
            final String title = keyword.getTitle();

            return parseBoolean(isNullOrEmpty(key) ? title : key);
        }
    };

    private static final Converter<KeywordModel> KEYWORD_CONVERTER = new KeywordConverter();

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<Keyword> keywords = field.getKeywordValues();

        final List<Object> values = new ArrayList<>(keywords.size());

        Converter<?> converter = getConverter(targetClass);

        for (Keyword keyword : keywords) {
            values.add(converter.convert(keyword));
        }

        return values;
    }

    private Converter<?> getConverter(Class<?> targetClass) throws UnsupportedTargetTypeException {
        Converter<?> converter;
        if (targetClass.isAssignableFrom(Tag.class)) {
            converter = TAG_CONVERTER;
        } else if (targetClass.isAssignableFrom(Boolean.class)) {
            converter = BOOLEAN_CONVERTER;
        } else if (targetClass.isAssignableFrom(String.class)) {
            converter = STRING_CONVERTER;
        } else if (targetClass.isAssignableFrom(KeywordModel.class)) {
            converter = KEYWORD_CONVERTER;
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
        return converter;
    }

    @FunctionalInterface
    private interface Converter<T> {

        T convert(Keyword keyword);

        default String getKeywordDisplayText(Keyword keyword) {
            return isNullOrEmpty(keyword.getDescription()) ? keyword.getTitle() : keyword.getDescription();
        }

        default String getKeywordKey(Keyword keyword) {
            return isNullOrEmpty(keyword.getKey()) ? keyword.getId() : keyword.getKey();
        }
    }

    private static class KeywordConverter implements Converter<KeywordModel> {

        private String getMetadataSchemaId(Keyword keyword) {
            if (keyword.getExtensionData() == null ||
                    !keyword.getExtensionData().containsKey("DXA") ||
                    !keyword.getExtensionData().get("DXA").getContent().containsKey("MetadataSchemaId")) {
                return null;
            }

            return (String) keyword.getExtensionData().get("DXA").getContent().get("MetadataSchemaId").getValues().get(0);
        }

        @Override
        public KeywordModel convert(Keyword keyword) {
            KeywordModel keywordModel;
            if (isNullOrEmpty(getMetadataSchemaId(keyword))) {
                keywordModel = new KeywordModel();
            } else {
                throw new UnsupportedOperationException();
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

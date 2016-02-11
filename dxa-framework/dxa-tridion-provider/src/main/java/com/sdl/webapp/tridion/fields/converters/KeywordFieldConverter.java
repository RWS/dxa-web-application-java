package com.sdl.webapp.tridion.fields.converters;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.entity.Tag;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
/**
 * <p>KeywordFieldConverter class.</p>
 */
public class KeywordFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.KEYWORD};

    private static List<Tag> getTagValues(List<Keyword> keywordValues) {
        final List<Tag> tagValues = new ArrayList<>();
        for (Keyword keyword : keywordValues) {
            final Tag tag = new Tag();
            tag.setDisplayText(getKeywordDisplayText(keyword));
            tag.setKey(getKeywordKey(keyword));
            tag.setTagCategory(keyword.getTaxonomyId());
            tagValues.add(tag);
        }
        return tagValues;
    }

    private static List<Boolean> getBooleanValues(List<Keyword> keywordValues) {
        final List<Boolean> booleanValues = new ArrayList<>();
        for (Keyword keyword : keywordValues) {
            final String key = keyword.getKey();
            final String title = keyword.getTitle();
            booleanValues.add(Boolean.parseBoolean(Strings.isNullOrEmpty(key) ? title : key));
        }
        return booleanValues;
    }

    private static List<String> getStringValues(List<Keyword> keywordValues) {
        final List<String> stringValues = new ArrayList<>();
        for (Keyword keyword : keywordValues) {
            stringValues.add(getKeywordDisplayText(keyword));
        }
        return stringValues;

    }

    private static String getKeywordDisplayText(Keyword keyword) {
        return Strings.isNullOrEmpty(keyword.getDescription()) ? keyword.getTitle() : keyword.getDescription();
    }

    private static String getKeywordKey(Keyword keyword) {
        return Strings.isNullOrEmpty(keyword.getKey()) ? keyword.getId() : keyword.getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<Keyword> keywordValues = field.getKeywordValues();

        if (targetClass.isAssignableFrom(Tag.class)) {
            return getTagValues(keywordValues);
        } else if (targetClass.isAssignableFrom(Boolean.class)) {
            return getBooleanValues(keywordValues);
        } else if (targetClass.isAssignableFrom(String.class)) {
            return getStringValues(keywordValues);
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
    }
}

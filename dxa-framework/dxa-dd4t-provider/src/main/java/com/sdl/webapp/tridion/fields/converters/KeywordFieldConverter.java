package com.sdl.webapp.tridion.fields.converters;

import com.sdl.dxa.tridion.mapping.converter.source.keyword.Converter;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.Tag;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.dxa.tridion.mapping.converter.source.keyword.Converter.getConverter;
import static com.sdl.webapp.common.util.StringUtils.toStrings;

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

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException {
        Class<?> targetClass = targetType.isCollection() ? targetType.getElementTypeDescriptor().getObjectType() : targetType.getObjectType();
        Converter<?> converter = getConverter(targetClass);

        final List<Keyword> keywords = field.getKeywordValues();
        return semanticField.isMultiValue() && targetType.isCollection() ?
                collect(keywords, converter) :
                keywords.isEmpty() ? null : converter.convert(toWrapper(keywords.get(0)));
    }

    @Override
    public List<String> getStringValues(BaseField field) throws FieldConverterException {
        return toStrings(collect(field.getKeywordValues(), getConverter(String.class)));
    }

    private List<?> collect(List<Keyword> keywords, Converter<?> converter) throws FieldConverterException {
        List<Object> list = new ArrayList<>();
        for (Keyword keyword : keywords) {
            list.add(converter.convert(toWrapper(keyword)));
        }
        return list;
    }

    private Converter.KeywordWrapper toWrapper(Keyword keyword) {
        return new Converter.KeywordWrapper() {
            @Override
            public String getDescription() {
                return keyword.getDescription();
            }

            @Override
            public String getTitle() {
                return keyword.getTitle();
            }

            @Override
            public String getKey() {
                return keyword.getKey();
            }

            @Override
            public String getId() {
                return String.valueOf(TcmUtils.getItemId(keyword.getId()));
            }

            @Override
            public String getTaxonomyId() {
                return String.valueOf(TcmUtils.getItemId(keyword.getTaxonomyId()));
            }

            @Override
            public String getSchemaId() {
                String key = "DXA";
                String contentKey = "MetadataSchemaId";
                if (keyword.getExtensionData() == null || !keyword.getExtensionData().containsKey(key) ||
                        !keyword.getExtensionData().get(key).getContent().containsKey(contentKey)) {
                    return null;
                }
                return String.valueOf(TcmUtils.getItemId((String) keyword.getExtensionData().get(key).getContent().get(contentKey).getValues().get(0)));
            }

            @Override
            public SemanticFieldDataProvider getDataProvider() {
                return SemanticFieldDataProviderImpl.getFor(new SemanticFieldDataProviderImpl.KeywordEntity(keyword));
            }
        };
    }
}

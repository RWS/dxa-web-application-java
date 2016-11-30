package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

import static com.sdl.webapp.common.util.StringUtils.toStrings;

/**
 * Implementors of this interface are capable for converting DD4T's {@link BaseField} to the expected type.
 * The {@link FieldType} that is supported by the implementor is returned by {@link #supportedFieldTypes()}.
 */
public interface FieldConverter {

    FieldType[] supportedFieldTypes();

    Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                         SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException;

    /**
     * Default implementation to convert values of the field to the {@linkplain List list} of {@linkplain String strings}.
     *
     * @param field field to convert
     * @return list of strings
     * @since 1.7
     */
    default List<String> getStringValues(BaseField field) throws FieldConverterException {
        return toStrings(field.getValues());
    }
}

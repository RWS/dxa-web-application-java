package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.dd4t.DD4TSemanticFieldDataProvider;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

public abstract class AbstractFieldConverter implements FieldConverter {

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                DD4TSemanticFieldDataProvider semanticFieldDataProvider) throws FieldConverterException {
        final List<?> fieldValues = getFieldValues(field, targetType.isCollection() ?
                targetType.getElementTypeDescriptor().getObjectType() : targetType.getObjectType());

        return semanticField.isMultiValue() ? fieldValues :
                (fieldValues != null && !fieldValues.isEmpty() ? fieldValues.get(0) : null);
    }

    protected abstract List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException;
}

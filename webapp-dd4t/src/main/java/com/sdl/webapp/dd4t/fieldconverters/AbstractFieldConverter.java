package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFieldConverter implements FieldConverter {

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                SemanticFieldDataProvider semanticFieldDataProvider) throws FieldConverterException {
        final List<?> fieldValues = getFieldValues(field, targetType.isCollection() ?
                targetType.getElementTypeDescriptor().getObjectType() : targetType.getObjectType());

        return semanticField.isMultiValue() ? fieldValues : (fieldValues.isEmpty() ? null : fieldValues.get(0));
    }

    protected abstract List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException;
}

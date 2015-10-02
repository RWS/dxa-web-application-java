package com.sdl.webapp.tridion.fieldconverters;

import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.tridion.AbstractSemanticFieldDataProvider;

import com.sdl.webapp.tridion.ModelBuilderPipeline;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

public abstract class AbstractFieldConverter implements FieldConverter {

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                AbstractSemanticFieldDataProvider semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<?> fieldValues = getFieldValues(field, targetType.isCollection() ?
                targetType.getElementTypeDescriptor().getObjectType() : targetType.getObjectType(), builder);

        return semanticField.isMultiValue() ? fieldValues :
                (fieldValues != null && !fieldValues.isEmpty() ? fieldValues.get(0) : null);
    }

    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        return getFieldValues(field, targetClass, null);
    }

    protected abstract List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException;
}

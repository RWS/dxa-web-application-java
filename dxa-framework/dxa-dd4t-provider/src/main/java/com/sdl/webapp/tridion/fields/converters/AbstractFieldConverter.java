package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.apache.commons.collections.CollectionUtils;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

//todo dxa2 the whole set of converters should be generified
public abstract class AbstractFieldConverter implements FieldConverter {

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<?> fieldValues = getFieldValues(field, targetType.isCollection() ?
                targetType.getElementTypeDescriptor().getObjectType() : targetType.getObjectType(), builder);

        return semanticField.isMultiValue() && targetType.isCollection() ? fieldValues :
                (!CollectionUtils.isEmpty(fieldValues) ? fieldValues.get(0) : null);
    }

    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        return getFieldValues(field, targetClass, null);
    }

    protected abstract List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException;
}

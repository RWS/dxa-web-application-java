package com.sdl.webapp.dd4t.fieldconv;

import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

public abstract class AbstractFieldConverter implements FieldConverter {

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                SemanticFieldDataProvider semanticFieldDataProvider) throws FieldConverterException {
        return getFieldValue(semanticField, field, targetType);
    }

    protected abstract Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType)
            throws FieldConverterException;
}

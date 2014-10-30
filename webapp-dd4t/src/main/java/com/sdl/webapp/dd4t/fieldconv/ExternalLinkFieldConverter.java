package com.sdl.webapp.dd4t.fieldconv;

import com.sdl.webapp.common.api.mapping.config.SemanticField;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

@Component
public class ExternalLinkFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.ExternalLink };

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType)
            throws FieldConverterException {
        // TODO: Implement this method
        return null;
    }
}

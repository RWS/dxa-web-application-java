package com.sdl.webapp.dd4t.fieldconv;

import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

public interface FieldConverter {

    FieldType[] supportedFieldTypes();

    Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                         SemanticFieldDataProvider semanticFieldDataProvider) throws FieldConverterException;
}

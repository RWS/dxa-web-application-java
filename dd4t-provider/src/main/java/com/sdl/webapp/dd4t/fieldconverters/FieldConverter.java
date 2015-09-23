package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.dd4t.AbstractSemanticFieldDataProvider;
import com.sdl.webapp.dd4t.DD4TSemanticFieldDataProvider;
import com.sdl.webapp.dd4t.EntityBuilder;

import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

public interface FieldConverter {

    FieldType[] supportedFieldTypes();

    Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                         AbstractSemanticFieldDataProvider semanticFieldDataProvider, EntityBuilder builder) throws FieldConverterException;
}

package com.sdl.webapp.tridion.fieldconverters;

import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.ModelBuilderPipeline;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

public interface FieldConverter {

    FieldType[] supportedFieldTypes();

    Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                         SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException;
}

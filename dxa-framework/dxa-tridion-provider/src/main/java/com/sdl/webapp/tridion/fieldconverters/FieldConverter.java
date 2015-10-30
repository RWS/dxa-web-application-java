package com.sdl.webapp.tridion.fieldconverters;

import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.tridion.AbstractSemanticFieldDataProvider;
import com.sdl.webapp.tridion.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

public interface FieldConverter {

    FieldType[] supportedFieldTypes();

    Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                         AbstractSemanticFieldDataProvider semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException;
}

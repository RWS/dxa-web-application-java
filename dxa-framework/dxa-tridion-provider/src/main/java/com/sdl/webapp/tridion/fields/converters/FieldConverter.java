package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

/**
 * <p>FieldConverter interface.</p>
 */
public interface FieldConverter {

    /**
     * <p>supportedFieldTypes.</p>
     *
     * @return an array of {@link org.dd4t.contentmodel.FieldType} objects.
     */
    FieldType[] supportedFieldTypes();

    /**
     * <p>getFieldValue.</p>
     *
     * @param semanticField             a {@link com.sdl.webapp.common.api.mapping.semantic.config.SemanticField} object.
     * @param field                     a {@link org.dd4t.contentmodel.impl.BaseField} object.
     * @param targetType                a {@link org.springframework.core.convert.TypeDescriptor} object.
     * @param semanticFieldDataProvider a {@link com.sdl.webapp.tridion.SemanticFieldDataProviderImpl} object.
     * @param builder                   a {@link com.sdl.webapp.tridion.mapping.ModelBuilderPipeline} object.
     * @return a {@link java.lang.Object} object.
     * @throws com.sdl.webapp.tridion.fields.exceptions.FieldConverterException if any.
     */
    Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                         SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException;
}

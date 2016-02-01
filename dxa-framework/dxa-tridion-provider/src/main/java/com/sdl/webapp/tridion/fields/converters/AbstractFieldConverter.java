package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

/**
 * <p>Abstract AbstractFieldConverter class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public abstract class AbstractFieldConverter implements FieldConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<?> fieldValues = getFieldValues(field, targetType.isCollection() ?
                targetType.getElementTypeDescriptor().getObjectType() : targetType.getObjectType(), builder);

        return semanticField.isMultiValue() ? fieldValues :
                (fieldValues != null && !fieldValues.isEmpty() ? fieldValues.get(0) : null);
    }

    /**
     * <p>getFieldValues.</p>
     *
     * @param field       a {@link org.dd4t.contentmodel.impl.BaseField} object.
     * @param targetClass a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     * @throws com.sdl.webapp.tridion.fields.exceptions.FieldConverterException if any.
     */
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        return getFieldValues(field, targetClass, null);
    }

    /**
     * <p>getFieldValues.</p>
     *
     * @param field a {@link org.dd4t.contentmodel.impl.BaseField} object.
     * @param targetClass a {@link java.lang.Class} object.
     * @param builder a {@link com.sdl.webapp.tridion.mapping.ModelBuilderPipeline} object.
     * @return a {@link java.util.List} object.
     * @throws com.sdl.webapp.tridion.fields.exceptions.FieldConverterException if any.
     */
    protected abstract List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException;
}

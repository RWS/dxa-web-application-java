package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmbeddedFieldConverter implements FieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.EMBEDDED};

    private final SemanticMapper semanticMapper;

    @Autowired
    public EmbeddedFieldConverter(SemanticMapper semanticMapper) {
        this.semanticMapper = semanticMapper;
    }

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType,
                                SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<FieldSet> embeddedValues = field.getEmbeddedValues();

        if (semanticField.isMultiValue()) {
            final TypeDescriptor elementType = targetType.getElementTypeDescriptor();

            final List<Object> fieldValues = new ArrayList<>();
            for (FieldSet fieldSet : embeddedValues) {
                final Object fieldValue = getFieldValue(semanticField, fieldSet, elementType, semanticFieldDataProvider);
                if (fieldValue != null) {
                    fieldValues.add(fieldValue);
                }
            }

            return fieldValues;
        } else {
            return embeddedValues.isEmpty() ? null : getFieldValue(semanticField, embeddedValues.get(0), targetType,
                    semanticFieldDataProvider);
        }
    }

    private Object getFieldValue(SemanticField semanticField, FieldSet fieldSet, TypeDescriptor targetType,
                                 SemanticFieldDataProviderImpl semanticFieldDataProvider) throws FieldConverterException {
        final Class<?> targetClass = targetType.getObjectType();

        if (AbstractEntityModel.class.isAssignableFrom(targetClass)) {
            try {
                semanticFieldDataProvider.pushEmbeddingLevel(fieldSet.getContent());

                return semanticMapper.createEntity(targetClass.asSubclass(AbstractEntityModel.class),
                        semanticField.getEmbeddedFields(), semanticFieldDataProvider);
            } catch (SemanticMappingException e) {
                throw new FieldConverterException("Exception while creating entity for embedded field: " +
                        semanticField.getPath(), e);
            } finally {
                semanticFieldDataProvider.popEmbeddingLevel();
            }
        } else {
            // The type of an embedded field must extend AbstractEntity (or it must be a collection where the element
            // type extends AbstractEntity)
            throw new UnsupportedTargetTypeException(targetType);
        }
    }
}

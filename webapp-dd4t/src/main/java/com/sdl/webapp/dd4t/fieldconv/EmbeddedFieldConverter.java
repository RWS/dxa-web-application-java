package com.sdl.webapp.dd4t.fieldconv;

import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.Entity;
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

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.Embedded };

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
                                SemanticFieldDataProvider semanticFieldDataProvider) throws FieldConverterException {
        final List<FieldSet> embeddedValues = field.getEmbeddedValues();
        if (semanticField.isMultiValue()) {
            final TypeDescriptor elementType = targetType.getElementTypeDescriptor();

            final List<Object> fieldValues = new ArrayList<>();
            for (FieldSet fieldSet : embeddedValues) {
                final Object fieldValue = getFieldValue(semanticField, fieldSet, elementType,
                        semanticFieldDataProvider);
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
                                 SemanticFieldDataProvider semanticFieldDataProvider) throws FieldConverterException {
        final Class<?> targetClass = targetType.getType();
        if (!Entity.class.isAssignableFrom(targetClass)) {
            throw new UnsupportedTargetTypeException("The target field type for an embedded field must implement " +
                    "interface " + Entity.class.getName() + ", but something else was found instead: " +
                    targetClass.getName() + " - This most likely means the field type in the containing entity class " +
                    "is wrong.");
        }

        // TODO: Something is wrong here, because fieldSet is not used. How does this get the correct data?

        try {
            return semanticMapper.createEntity(targetClass.asSubclass(Entity.class), semanticField.getEmbeddedFields(),
                    semanticFieldDataProvider);
        } catch (SemanticMappingException e) {
            throw new FieldConverterException("Exception while creating entity for embedded field: " +
                    semanticField.getPath(), e);
        }
    }
}

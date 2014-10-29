package com.sdl.webapp.dd4t.pagefactory;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.Entity;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DD4TSemanticFieldDataProvider implements SemanticFieldDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TSemanticFieldDataProvider.class);

    private final GenericComponent component;

    private final SemanticMapper semanticMapper;

    public DD4TSemanticFieldDataProvider(GenericComponent component, SemanticMapper semanticMapper) {
        this.component = component;
        this.semanticMapper = semanticMapper;
    }

    @Override
    public Object getFieldData(SemanticField semanticField, TypeDescriptor targetType) throws SemanticMappingException {
        LOG.trace("semanticField: {}, targetType: {}", semanticField, targetType);

        final String[] parts = splitPath(semanticField.getPath());
        final String pathHead = parts[0];
        final String pathTail = parts[1];

        final Map<String, Field> fields = pathHead.equals("/Metadata") ? component.getMetadata() : component.getContent();

        final Field field = findField(pathTail, fields);
        if (field == null) {
            LOG.debug("No DD4T field found for: {}", semanticField);
            return null;
        }
        LOG.trace("Found DD4T field: [{}] {}", field.getFieldType(), field.getName());

        final Object fieldData;
        switch (field.getFieldType()) {
            case Text:
                fieldData = getTextFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            case MultiLineText:
                fieldData = getMultiLineTextFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            case Xhtml:
                fieldData = getXhtmlFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            case Keyword:
                fieldData = getKeywordFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            case Embedded:
                fieldData = getEmbeddedFieldValues((EmbeddedField) field, targetType, semanticField.isMultiValue(),
                        semanticField);
                break;

            case MultimediaLink:
                fieldData = getMultimediaLinkFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            case ComponentLink:
                fieldData = getComponentLinkFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            case ExternalLink:
                fieldData = getExternalLinkFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            case Number:
                fieldData = getNumberFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            case Date:
                fieldData = getDateFieldValues(field, targetType, semanticField.isMultiValue());
                break;

            default:
                throw new SemanticMappingException("Unsupported field type: " + field.getFieldType());
        }

        if (LOG.isTraceEnabled() && fieldData != null) {
            LOG.trace("Data for field {}: {}", semanticField.getPath(), fieldData);
        }

        if (fieldData == null) {
            LOG.debug("Empty data for field: {}", semanticField.getPath());
        }

        return fieldData;
    }

    private Field findField(String path, Map<String, Field> fields) {
        final String[] parts = splitPath(path);

        final Field field = fields.get(parts[0]);
        if (field == null) {
            return null;
        }

        if (Strings.isNullOrEmpty(parts[1])) {
            return field;
        } else {
            if (field instanceof EmbeddedField) {
                final List<FieldSet> embeddedValues = ((EmbeddedField) field).getEmbeddedValues();
                if (embeddedValues != null && !embeddedValues.isEmpty()) {
                    return findField(parts[1], embeddedValues.get(0).getContent());
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private String[] splitPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return new String[] { "", "" };
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        final int i = path.indexOf('/');
        if (i < 0) {
            return new String[] { path, "" };
        } else {
            return new String[] { path.substring(0, i), path.substring(i) };
        }
    }

    private Object getTextFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        return multiValue ? field.getValues() : field.getValues().get(0);
    }

    private Object getMultiLineTextFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        // TODO: Implement this method
        LOG.warn("Not yet implemented: getMultiLineTextFieldValues");
        return null;
    }

    private Object getXhtmlFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        // TODO: Implement this method
        LOG.warn("Not yet implemented: getXhtmlFieldValues");
        return null;
    }

    private Object getKeywordFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        // TODO: Implement this method
        LOG.warn("Not yet implemented: getKeywordFieldValues");
        return null;
    }

    private Object getEmbeddedFieldValues(EmbeddedField field, TypeDescriptor targetType, boolean multiValue,
                                          SemanticField semanticField) throws SemanticMappingException {
        if (multiValue) {
            List<Object> result = new ArrayList<>();
            for (FieldSet fieldSet : field.getEmbeddedValues()) {
                result.add(getEmbeddedFieldValue(fieldSet, targetType.getElementTypeDescriptor(), semanticField));
            }
            return result;
        } else {
            return getEmbeddedFieldValue(field.getEmbeddedValues().get(0), targetType, semanticField);
        }
    }

    private Object getEmbeddedFieldValue(FieldSet fieldSet, TypeDescriptor targetType, SemanticField semanticField)
            throws SemanticMappingException {
        @SuppressWarnings("unchecked")
        final Class<? extends Entity> embeddedEntityClass = (Class<? extends Entity>) targetType.getType();
        return semanticMapper.createEntity(embeddedEntityClass, semanticField.getEmbeddedFields(), this);
    }

    private Object getMultimediaLinkFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        // TODO: Implement this method
        LOG.warn("Not yet implemented: getMultimediaLinkFieldValues");
        return null;
    }

    private Object getExternalLinkFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        // TODO: Implement this method
        LOG.warn("Not yet implemented: getExternalLinkFieldValues");
        return null;
    }

    private Object getComponentLinkFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        // TODO: Implement this method
        LOG.warn("Not yet implemented: getComponentLinkFieldValues");
        return null;
    }

    private Object getNumberFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        // TODO: Implement this method
        LOG.warn("Not yet implemented: getNumberFieldValues");
        return null;
    }

    private Object getDateFieldValues(Field field, TypeDescriptor targetType, boolean multiValue) {
        // TODO: Implement this method
        LOG.warn("Not yet implemented: getDateFieldValues");
        return null;
    }
}

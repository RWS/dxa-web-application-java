package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.FieldData;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.FieldPath;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.dd4t.fieldconverters.*;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.impl.BaseField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Implementation of {@code SemanticFieldDataProvider} that gets field data from the DD4T model.
 */
public class DD4TSemanticFieldDataProvider implements SemanticFieldDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TSemanticFieldDataProvider.class);

    private static final String METADATA_PATH = "Metadata";

    private final org.dd4t.contentmodel.Component component;

    private final FieldConverterRegistry fieldConverterRegistry;

    private int embeddingLevel = 0;

    private final Stack<Map<String, Field>> embeddedFieldsStack = new Stack<>();

    public DD4TSemanticFieldDataProvider(org.dd4t.contentmodel.Component component, FieldConverterRegistry fieldConverterRegistry) {
        this.component = component;
        this.fieldConverterRegistry = fieldConverterRegistry;
    }

    public void pushEmbeddingLevel(Map<String, Field> embeddedFields) {
        embeddingLevel++;
        embeddedFieldsStack.push(embeddedFields);
    }

    public void popEmbeddingLevel() {
        embeddingLevel--;
        embeddedFieldsStack.pop();
    }

    @Override
    public FieldData getFieldData(SemanticField semanticField, TypeDescriptor targetType) throws SemanticMappingException {
        LOG.trace("semanticField: {}, targetType: {}", semanticField, targetType);

        final Map<String, Field> fields;
        FieldPath path = semanticField.getPath();

        if (embeddingLevel > 0) {
            // Embedded field: get the current set of embedded fields, cut embeddingLevel + 1 levels off of the path
            fields = embeddedFieldsStack.peek();
            for (int i = 0; i < embeddingLevel + 1 && path != null; i++) {
                path = path.getTail();
            }
        } else {
            // Top-level field: look either in the metadata or the content of the component
            fields = path.getHead().equals(METADATA_PATH) ? component.getMetadata() : component.getContent();
            path = path.getTail();
        }

        final Field field = findField(path, fields);
        if (field == null) {
            LOG.debug("No DD4T field found for: {}", semanticField);
            return null;
        }
        LOG.trace("Found DD4T field: [{}] {}", field.getFieldType(), field.getName());

        final Object fieldValue = fieldConverterRegistry.getFieldConverterFor(field.getFieldType())
                .getFieldValue(semanticField, (BaseField) field, targetType, this);

        return new FieldData(fieldValue, field.getXPath());
    }

    @Override
    public Object getSelfFieldData(TypeDescriptor targetType) throws SemanticMappingException {
        final Class<?> targetClass = targetType.getObjectType();

        if (MediaItem.class.isAssignableFrom(targetClass)) {
            return ((MultimediaLinkFieldConverter) fieldConverterRegistry.getFieldConverterFor(
                    FieldType.MULTIMEDIALINK)).createMediaItem(component, targetClass);
        } else if (Link.class.isAssignableFrom(targetClass) || String.class.isAssignableFrom(targetClass)) {
            return ((ComponentLinkFieldConverter) fieldConverterRegistry.getFieldConverterFor(
                    FieldType.COMPONENTLINK)).createComponentLink(component, targetClass);
        } else {
            throw new UnsupportedTargetTypeException(targetType);
        }
    }

    @Override
    public Map<String, String> getAllFieldData() throws SemanticMappingException {
        final Map<String, String> fieldData = new HashMap<>();

        // Add content fields as text
        for (Map.Entry<String, Field> entry : component.getContent().entrySet()) {
            final String name = entry.getKey();
            if (!fieldData.containsKey(name)) {
                final BaseField field = (BaseField) entry.getValue();

                // Special case for "settings" field
                if (name.equals("settings") && field.getFieldType() == FieldType.EMBEDDED) {
                    for (FieldSet fieldSet : field.getEmbeddedValues()) {
                        final Map<String, Field> fieldSetContent = fieldSet.getContent();
                        final String key = FieldUtils.getStringValue(fieldSetContent, "name");
                        if (!Strings.isNullOrEmpty(key) && !fieldData.containsKey(key)) {
                            final String value = FieldUtils.getStringValue(fieldSetContent, "value");
                            if (!Strings.isNullOrEmpty(value)) {
                                fieldData.put(key, value);
                            }
                        }
                    }
                } else {
                    final String value = FieldUtils.getStringValue(field);
                    if (!Strings.isNullOrEmpty(value)) {
                        fieldData.put(name, value);
                    }
                }
            }
        }

        // Add metadata fields as text
        for (Map.Entry<String, Field> entry : component.getMetadata().entrySet()) {
            final String name = entry.getKey();
            if (!fieldData.containsKey(name)) {
                final String value = FieldUtils.getStringValue(entry.getValue());
                if (!Strings.isNullOrEmpty(value)) {
                    fieldData.put(name, value);
                }
            }
        }

        return fieldData;
    }

    private Field findField(FieldPath path, Map<String, Field> fields) {
        if (path == null) {
            return null;
        }

        final Field field = fields.get(path.getHead());
        if (!path.hasTail() || field == null) {
            return field;
        }

        if (field.getFieldType() == FieldType.EMBEDDED) {
            final List<FieldSet> embeddedValues = ((BaseField) field).getEmbeddedValues();
            if (embeddedValues != null && !embeddedValues.isEmpty()) {
                return findField(path.getTail(), embeddedValues.get(0).getContent());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

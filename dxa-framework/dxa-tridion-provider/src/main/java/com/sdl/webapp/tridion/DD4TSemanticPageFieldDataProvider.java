package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.FieldData;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.FieldPath;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.tridion.fieldconverters.ComponentLinkFieldConverter;
import com.sdl.webapp.tridion.fieldconverters.FieldConverterRegistry;
import com.sdl.webapp.tridion.fieldconverters.FieldUtils;
import com.sdl.webapp.tridion.fieldconverters.UnsupportedTargetTypeException;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 16/09/2015.
 */
public class DD4TSemanticPageFieldDataProvider extends AbstractSemanticFieldDataProvider implements SemanticFieldDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TSemanticPageFieldDataProvider.class);


    public DD4TSemanticPageFieldDataProvider(org.dd4t.contentmodel.Page page, FieldConverterRegistry fieldConverterRegistry, ModelBuilderPipeline builder) {
        super(page, fieldConverterRegistry, builder);
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
            fields = path.getHead().equals(METADATA_PATH) ? page.getMetadata() : null;
            path = path.getTail();
        }

        if (fields == null) {
            LOG.debug("Page has no metadata - no DD4T field found for: {}", semanticField);
            return null;
        }

        final Field field = findField(path, fields);
        if (field == null) {
            LOG.debug("No DD4T field found for: {}", semanticField);
            return null;
        }
        LOG.trace("Found DD4T field: [{}] {}", field.getFieldType(), field.getName());

        final Object fieldValue = fieldConverterRegistry.getFieldConverterFor(field.getFieldType())
                .getFieldValue(semanticField, (BaseField) field, targetType, this, this.builder);

        return new FieldData(fieldValue, field.getXPath());
    }

    @Override
    public Object getSelfFieldData(TypeDescriptor targetType) throws SemanticMappingException {
        final Class<?> targetClass = targetType.getObjectType();

        if (Link.class.isAssignableFrom(targetClass) || String.class.isAssignableFrom(targetClass)) {
            return ((ComponentLinkFieldConverter) fieldConverterRegistry.getFieldConverterFor(
                    FieldType.COMPONENTLINK)).createPageLink(this.page, targetClass);
        } else {
            throw new UnsupportedTargetTypeException(targetType);
        }
    }

    @Override
    public Map<String, String> getAllFieldData() throws SemanticMappingException {
        final Map<String, String> fieldData = new HashMap<>();

        // Add content fields as text
        for (Map.Entry<String, Field> entry : page.getMetadata().entrySet()) {
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

        return fieldData;
    }
}


package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.semantic.FieldData;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldPath;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.dd4t.util.FieldUtils;
import com.sdl.webapp.tridion.fields.FieldConverterRegistry;
import com.sdl.webapp.tridion.fields.converters.ComponentLinkFieldConverter;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import lombok.ToString;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.BaseField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SemanticFieldDataProviderImpl implements SemanticFieldDataProvider {
    protected static final String METADATA_PATH = "Metadata";
    private static final Logger LOG = LoggerFactory.getLogger(SemanticFieldDataProviderImpl.class);
    protected final SemanticEntity semanticEntity;

    protected final FieldConverterRegistry fieldConverterRegistry;

    protected final ModelBuilderPipeline builder;

    protected final Stack<Map<String, Field>> embeddedFieldsStack = new Stack<>();

    protected int embeddingLevel = 0;

    public SemanticFieldDataProviderImpl(SemanticEntity semanticEntity, FieldConverterRegistry fieldConverterRegistry, ModelBuilderPipeline builder) {
        this.semanticEntity = semanticEntity;
        this.semanticEntity.injectDataProvider(this);
        this.fieldConverterRegistry = fieldConverterRegistry;
        this.builder = builder;
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
            fields = semanticEntity.getFields(path);
            path = path.getTail();
        }

        if (CollectionUtils.isEmpty(fields)) {
            LOG.debug("No metadata found for {} - no DD4T field found for: {}", semanticEntity, semanticField);
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

        if (MediaItem.class.isAssignableFrom(targetClass) || Link.class.isAssignableFrom(targetClass) || String.class.isAssignableFrom(targetClass)) {
            return semanticEntity.createLink(targetClass);
        } else {
            throw new UnsupportedTargetTypeException(targetType);
        }
    }

    @Override
    public Map<String, String> getAllFieldData() throws SemanticMappingException {
        final Map<String, String> fieldData = new HashMap<>();

        // Add content fields as text
        for (Map.Entry<String, Field> entry : semanticEntity.getFields().entrySet()) {
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
        for (Map.Entry<String, Field> entry : semanticEntity.getFields().entrySet()) {
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

    protected Field findField(FieldPath path, Map<String, Field> fields) {
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

    public interface SemanticEntity {
        Map<String, Field> getFields();
        Map<String, Field> getFields(FieldPath path);
        Object createLink(Class<?> targetClass) throws SemanticMappingException;
        void injectDataProvider(SemanticFieldDataProviderImpl fieldDataProvider);
    }

    @ToString
    public static class PageEntity implements SemanticEntity {
        private org.dd4t.contentmodel.Page page;
        private SemanticFieldDataProviderImpl fieldDataProvider;

        public PageEntity(Page page) {
            this.page = page;
        }

        @Override
        public Map<String, Field> getFields() {
            return page.getMetadata();
        }

        @Override
        public Map<String, Field> getFields(FieldPath path) {
            return path.getHead().equals(METADATA_PATH) ? getFields() : Collections.<String, Field>emptyMap();
        }

        @Override
        public Object createLink(Class<?> targetClass) throws FieldConverterException {
            return ((ComponentLinkFieldConverter) fieldDataProvider.fieldConverterRegistry.getFieldConverterFor(
                    FieldType.COMPONENTLINK)).createPageLink(this.page, targetClass);
        }

        @Override
        public void injectDataProvider(SemanticFieldDataProviderImpl fieldDataProvider) {
            this.fieldDataProvider = fieldDataProvider;
        }
    }

    @ToString
    public static class ComponentEntity implements SemanticEntity {
        private Component component;
        private SemanticFieldDataProviderImpl fieldDataProvider;

        public ComponentEntity(Component component) {
            this.component = component;
        }

        @Override
        public Map<String, Field> getFields() {
            return component.getContent();
        }

        @Override
        public Map<String, Field> getFields(FieldPath path) {
            return path.getHead().equals(METADATA_PATH) ? component.getMetadata() : getFields();
        }

        @Override
        public Object createLink(Class<?> targetClass) throws SemanticMappingException {
            return ((ComponentLinkFieldConverter) fieldDataProvider.fieldConverterRegistry.getFieldConverterFor(
                    FieldType.COMPONENTLINK)).createComponentLink(component, targetClass, fieldDataProvider.builder);
        }

        @Override
        public void injectDataProvider(SemanticFieldDataProviderImpl fieldDataProvider) {
            this.fieldDataProvider = fieldDataProvider;
        }
    }
}

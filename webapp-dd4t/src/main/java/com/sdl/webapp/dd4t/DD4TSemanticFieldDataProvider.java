package com.sdl.webapp.dd4t;

import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.FieldPath;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.EmbeddedLink;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.dd4t.fieldconverters.ComponentLinkFieldConverter;
import com.sdl.webapp.dd4t.fieldconverters.FieldConverterRegistry;
import com.sdl.webapp.dd4t.fieldconverters.MultimediaLinkFieldConverter;
import com.sdl.webapp.dd4t.fieldconverters.UnsupportedTargetTypeException;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.impl.BaseField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Implementation of {@code SemanticFieldDataProvider} that gets field data from the DD4T model.
 */
public class DD4TSemanticFieldDataProvider implements SemanticFieldDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TSemanticFieldDataProvider.class);

    private static final String METADATA_PATH = "Metadata";

    private final GenericComponent component;

    private final FieldConverterRegistry fieldConverterRegistry;

    private int embeddingLevel = 0;

    private final Stack<Map<String, Field>> embeddedFieldsStack = new Stack<>();

    public DD4TSemanticFieldDataProvider(GenericComponent component, FieldConverterRegistry fieldConverterRegistry) {
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
    public Object getFieldData(SemanticField semanticField, TypeDescriptor targetType) throws SemanticMappingException {
        LOG.trace("semanticField: {}, targetType: {}", semanticField, targetType);

        final Map<String, Field> fields;
        FieldPath path = semanticField.getPath();

        if (embeddingLevel > 0) {
            fields = embeddedFieldsStack.peek();
            for (int i = 0; i < embeddingLevel + 1 && path != null; i++) {
                path = path.getTail();
            }
        } else {
            fields = path.getHead().equals(METADATA_PATH) ? component.getMetadata() : component.getContent();
            path = path.getTail();
        }

        final Field field = findField(path, fields);
        if (field == null) {
            LOG.debug("No DD4T field found for: {}", semanticField);
            return null;
        }
        LOG.trace("Found DD4T field: [{}] {}", field.getFieldType(), field.getName());

        return fieldConverterRegistry.getFieldConverterFor(field.getFieldType())
                .getFieldValue(semanticField, (BaseField) field, targetType, this);
    }

    @Override
    public Object getSelfPropertyData(TypeDescriptor targetType) throws SemanticMappingException {
        final Class<?> targetClass = targetType.getObjectType();

        if (MediaItem.class.isAssignableFrom(targetClass)) {
            return ((MultimediaLinkFieldConverter) fieldConverterRegistry.getFieldConverterFor(
                    FieldType.MultimediaLink)).createMediaItem(component, targetClass);
        } else if (EmbeddedLink.class.isAssignableFrom(targetClass) || String.class.isAssignableFrom(targetClass)) {
            return ((ComponentLinkFieldConverter) fieldConverterRegistry.getFieldConverterFor(
                    FieldType.ComponentLink)).createComponentLink(component, targetClass);
        } else {
            throw new UnsupportedTargetTypeException(targetType);
        }
    }

    @Override
    public Map<String, ?> getAllPropertyData() throws SemanticMappingException {
        // TODO: Implement this method
        throw new SemanticMappingException("Not yet implemented");
    }

    private Field findField(FieldPath path, Map<String, Field> fields) {
        if (path == null) {
            return null;
        }

        final Field field = fields.get(path.getHead());
        if (!path.hasTail() || field == null) {
            return field;
        }

        if (field.getFieldType() == FieldType.Embedded) {
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

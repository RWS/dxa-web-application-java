package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.FieldPath;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.EmbeddedLink;
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

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@code SemanticFieldDataProvider} that gets field data from the DD4T model.
 */
public class DD4TSemanticFieldDataProvider implements SemanticFieldDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TSemanticFieldDataProvider.class);

    private static final String METADATA_PATH = "Metadata";

    private final GenericComponent component;

    private final FieldConverterRegistry fieldConverterRegistry;

    public DD4TSemanticFieldDataProvider(GenericComponent component, FieldConverterRegistry fieldConverterRegistry) {
        this.component = component;
        this.fieldConverterRegistry = fieldConverterRegistry;
    }

    @Override
    public Object getFieldData(SemanticField semanticField, TypeDescriptor targetType) throws SemanticMappingException {
        LOG.trace("semanticField: {}, targetType: {}", semanticField, targetType);

        final FieldPath path = semanticField.getPath();
        final Field field = findField(path.getTail(),
                path.getHead().equals(METADATA_PATH) ? component.getMetadata() : component.getContent());
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
                return findField(path.getTail(), embeddedValues.get(0).getContent()); // TODO: altijd de eerste is niet goed!
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

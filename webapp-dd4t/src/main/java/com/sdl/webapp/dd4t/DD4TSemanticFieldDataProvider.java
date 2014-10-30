package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.dd4t.fieldconverters.FieldConverterException;
import com.sdl.webapp.dd4t.fieldconverters.FieldConverterRegistry;
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

        final String[] parts = splitPath(semanticField.getPath());
        final String pathHead = parts[0];
        final String pathTail = parts[1];

        final Field field = findField(pathTail,
                pathHead.equals(METADATA_PATH) ? component.getMetadata() : component.getContent());
        if (field == null) {
            LOG.debug("No DD4T field found for: {}", semanticField);
            return null;
        }
        LOG.trace("Found DD4T field: [{}] {}", field.getFieldType(), field.getName());

        try {
            return fieldConverterRegistry.getFieldConverterFor(field.getFieldType())
                    .getFieldValue(semanticField, (BaseField) field, targetType, this);
        } catch (FieldConverterException e) {
            throw new SemanticMappingException(e);
        }
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
            if (field.getFieldType() == FieldType.Embedded) {
                final List<FieldSet> embeddedValues = ((BaseField) field).getEmbeddedValues();
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
}

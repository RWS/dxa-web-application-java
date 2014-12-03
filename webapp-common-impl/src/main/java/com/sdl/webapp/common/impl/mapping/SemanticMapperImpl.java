package com.sdl.webapp.common.impl.mapping;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.mapping.*;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@code SemanticMapper}.
 */
@Component
public class SemanticMapperImpl implements SemanticMapper {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticMapperImpl.class);

    private static final String ALL_PROPERTY = "_all";
    private static final String SELF_PROPERTY = "_self";

    private final SemanticMappingRegistry registry;

    @Autowired
    public SemanticMapperImpl(SemanticMappingRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <T extends AbstractEntity> T createEntity(Class<? extends T> entityClass,
                                                     final Map<FieldSemantics, SemanticField> semanticFields,
                                                     final SemanticFieldDataProvider fieldDataProvider)
            throws SemanticMappingException {
        final T entity = createInstance(entityClass);

        final ImmutableMap.Builder<String, String> propertyDataBuilder = ImmutableMap.builder();

        // Map all the fields (including fields inherited from superclasses) of the entity
        ReflectionUtils.doWithFields(entityClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                // Find the semantics for this field
                final List<FieldSemantics> registrySemantics = registry.getFieldSemantics(field);
                if (LOG.isTraceEnabled() && !registrySemantics.isEmpty()) {
                    LOG.trace("field: {}", field);
                }

                boolean foundMatch = false;

                // Try getting data using each of the field semantics in order
                for (FieldSemantics fieldSemantics : registrySemantics) {
                    // Find the matching semantic field
                    final SemanticField semanticField = findMatchingSemanticField(semanticFields, fieldSemantics);
                    if (semanticField != null) {
                        foundMatch = true;
                        LOG.trace("Match found: {} -> {}", fieldSemantics, semanticField);

                        FieldData fieldData = null;
                        try {
                            fieldData = fieldDataProvider.getFieldData(semanticField, new TypeDescriptor(field));
                        } catch (SemanticMappingException e) {
                            LOG.error("Exception while getting field data for: " + field, e);
                        }

                        if (fieldData != null) {
                            final Object fieldValue = fieldData.getFieldValue();
                            if (fieldValue != null) {
                                if (LOG.isTraceEnabled()) {
                                    LOG.trace("Setting field value: {} -> {}", field.getName(), fieldValue);
                                }

                                field.setAccessible(true);
                                field.set(entity, fieldValue);

                                final String propertyData = fieldData.getPropertyData();
                                if (!Strings.isNullOrEmpty(propertyData)) {
                                    propertyDataBuilder.put(field.getName(), propertyData);
                                }

                                break;
                            }
                        }
                    }
                }

                // Special cases - only try these when nothing was found yet
                if (!foundMatch) {
                    for (FieldSemantics fieldSemantics : registrySemantics) {
                        final String propertyName = fieldSemantics.getPropertyName();
                        if (propertyName.equals(SELF_PROPERTY)) {
                            foundMatch = true;
                            Object fieldData = null;
                            try {
                                fieldData = fieldDataProvider.getSelfFieldData(new TypeDescriptor(field));
                            } catch (SemanticMappingException e) {
                                LOG.error("Exception while getting self property data for: " + field, e);
                            }

                            if (fieldData != null) {
                                field.setAccessible(true);
                                field.set(entity, fieldData);
                                break;
                            }
                        } else if (propertyName.equals(ALL_PROPERTY)) {
                            foundMatch = true;
                            Map<String, String> fieldData = null;
                            try {
                                fieldData = fieldDataProvider.getAllFieldData();
                            } catch (SemanticMappingException e) {
                                LOG.error("Exception while getting all property data for: " + field, e);
                            }

                            if (fieldData != null) {
                                field.setAccessible(true);
                                field.set(entity, fieldData);
                                break;
                            }
                        }
                    }
                }

                if (LOG.isDebugEnabled() && !foundMatch && !registrySemantics.isEmpty()) {
                    // This not necessarily means there is a problem; for some components in the input, not all fields
                    // of the entity are mapped
                    LOG.trace("No match found for field: {}; registry semantics: {} did not match with supplied " +
                            "semantics: {}", new Object[]{field, registrySemantics, semanticFields});
                }
            }
        });

        // Set property data (used for semantic markup)
        entity.setPropertyData(propertyDataBuilder.build());

        LOG.trace("entity: {}", entity);
        return entity;
    }

    private <T extends AbstractEntity> T createInstance(Class<? extends T> entityClass) throws SemanticMappingException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("entityClass: {}", entityClass.getName());
        }
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SemanticMappingException("Exception while creating instance of entity class: " +
                    entityClass.getName(), e);
        }
    }

    private SemanticField findMatchingSemanticField(Map<FieldSemantics, SemanticField> semanticFields,
                                                    FieldSemantics fieldSemantics) {
        SemanticField matchingField = semanticFields.get(fieldSemantics);
        if (matchingField == null) {
            // Search all embedded fields recursively
            for (SemanticField semanticField : semanticFields.values()) {
                matchingField = findMatchingSemanticField(semanticField.getEmbeddedFields(), fieldSemantics);
                if (matchingField != null) {
                    break;
                }
            }
        }

        return matchingField;
    }
}

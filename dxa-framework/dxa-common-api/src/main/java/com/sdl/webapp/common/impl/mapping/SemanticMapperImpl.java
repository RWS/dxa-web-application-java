package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.semantic.FieldData;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.config.EntitySemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.KeywordModel;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class SemanticMapperImpl implements SemanticMapper {

    private static final Logger LOG = LoggerFactory.getLogger(SemanticMapperImpl.class);

    private static final String ALL_PROPERTY = "_all";

    private static final String SELF_PROPERTY = "_self";

    private final SemanticMappingRegistry registry;

    /**
     * <p>Constructor for SemanticMapperImpl.</p>
     *
     * @param registry a {@link SemanticMappingRegistry} object.
     */
    @Autowired
    public SemanticMapperImpl(SemanticMappingRegistry registry) {
        this.registry = registry;
    }

    private static <T extends ViewModel> T createInstance(Class<? extends T> entityClass) throws SemanticMappingException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("entityClass: {}", entityClass.getName());
        }
        if (entityClass == null) {
            throw new SemanticMappingException("Cannot create entity because entity class is null, have you added all modules you need?");
        }
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SemanticMappingException("Exception while creating instance of entity class: " +
                    entityClass.getName(), e);
        }
    }

    private static SemanticField findFieldForGivenSemantics(Map<FieldSemantics, SemanticField> fields, FieldSemantics semantics) {

        SemanticField field = fields.get(semantics);

        if (field != null) {
            return field;
        }

        for (Map.Entry<FieldSemantics, SemanticField> entry : fields.entrySet()) {
            FieldSemantics key = entry.getKey();

            if (key.isStandardMetadataField() && Objects.equals(key.getPropertyName(), semantics.getPropertyName())) {
                return entry.getValue();
            }
        }

        // Search all embedded fields recursively
        for (SemanticField semanticField : fields.values()) {
            field = findFieldForGivenSemantics(semanticField.getEmbeddedFields(), semantics);

            if (field != null) {
                return field;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends ViewModel> T createEntity(Class<? extends T> entityClass,
                                                final Map<FieldSemantics, SemanticField> semanticFields,
                                                final SemanticFieldDataProvider fieldDataProvider)
            throws SemanticMappingException {
        final T entity = createInstance(entityClass);

        final Map<String, String> xpmPropertyMetadata = new HashMap<>();

        // Map all the fields (including fields inherited from superclasses) of the entity
        ReflectionUtils.doWithFields(entityClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                // Find the semantics for this field
                final Set<FieldSemantics> registrySemantics = registry.getFieldSemantics(field);
                if (LOG.isTraceEnabled() && !registrySemantics.isEmpty()) {
                    LOG.trace("field: {}", field);
                }

                boolean foundMatch = false;

                // Try getting data using each of the field semantics in order
                for (FieldSemantics fieldSemantics : registrySemantics) {
                    // Find the matching semantic field
                    final SemanticField semanticField = findFieldForGivenSemantics(semanticFields, fieldSemantics);
                    if (semanticField != null) {
                        foundMatch = true;
                        LOG.trace("Match found: {} -> {}", fieldSemantics, semanticField);

                        FieldData fieldData = null;
                        try {
                            fieldData = fieldDataProvider.getFieldData(semanticField, new TypeDescriptor(field));
                        } catch (SemanticMappingException e) {
                            LOG.error("Exception while getting field data for: " + field, e);
                        }

                        String xPath = null;
                        boolean isFieldSet = false;
                        if (fieldData != null) {
                            final Object fieldValue = fieldData.getFieldValue();
                            if (fieldValue != null) {
                                if (LOG.isTraceEnabled()) {
                                    LOG.trace("Setting field value: {} -> {}", field.getName(), fieldValue);
                                }

                                field.setAccessible(true);
                                if (field.getType().equals(RichText.class) && fieldValue.getClass().equals(String.class)) {
                                    field.set(entity, new RichText((String) fieldValue));
                                } else {
                                    field.set(entity, fieldValue);
                                }

                                xPath = fieldData.getPropertyData();
                                isFieldSet = true;
                            }
                        }

                        if (xPath == null) {
                            xPath = semanticField.getXPath("");
                        }

                        if (findFieldForGivenSemantics(fieldDataProvider.getSemanticSchema().getSemanticFields(), fieldSemantics) != null) {
                            xpmPropertyMetadata.put(field.getName(), xPath);
                        }
                        if (isFieldSet) {
                            break;
                        }
                    }
                }

                // Special cases - only try these when nothing was found yet
                if (!foundMatch) {
                    for (FieldSemantics fieldSemantics : registrySemantics) {
                        final String propertyName = fieldSemantics.getPropertyName();
                        SemanticSchema semanticSchema = fieldDataProvider.getSemanticSchema();
                        if (propertyName.equals(SELF_PROPERTY) && (semanticSchema == null ||
                                semanticSchema.hasSemantics(new EntitySemantics(fieldSemantics.getVocabulary(), fieldSemantics.getEntityName())))) {
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
                            
                            Map<String, ?> fieldData = null; 
                            
                            try 
                            {                   
                                if(IsTypeOfMap(String.class, KeywordModel.class, field)) 
                                {
                                    fieldData = fieldDataProvider.<KeywordModel>getAllFieldData(KeywordModel.class);
                                }
                                else
                                {
                                    fieldData  = fieldDataProvider.getAllFieldData(String.class);
                                }                  
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
                            "semantics: {}", field, registrySemantics, semanticFields);
                }
            }
        });

        // Set property data (used for semantic markup)
        if (AbstractEntityModel.class.isAssignableFrom(entity.getClass())) {
            ((AbstractEntityModel) entity).setXpmPropertyMetadata(xpmPropertyMetadata);
        }
        LOG.trace("entity: {}", entity);
        return entity;
    }

    private static boolean IsTypeOfMap(Type mapKeyType, Type mapValueType, Field field)
    {
        if( field.getType() == Map.class)
        {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            Type key = type.getActualTypeArguments()[0];
            Type value = type.getActualTypeArguments()[1];

            if (key == mapKeyType && value ==mapValueType)
            {
                return  true;
            }
        }

        return  false;
    }


}

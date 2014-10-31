package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final SemanticMappingRegistry registry = new SemanticMappingRegistry();

    public SemanticMapperImpl() throws SemanticMappingException {
        this.registry.registerEntities(AbstractEntity.class.getPackage().getName());
    }

    @Override
    public Entity createEntity(Class<? extends Entity> entityClass,
                               final Map<FieldSemantics, SemanticField> semanticFields,
                               final SemanticFieldDataProvider fieldDataProvider) throws SemanticMappingException {
        final Entity entity = createInstance(entityClass);

        // Map all the fields (including fields inherited from superclasses) of the entity
        ReflectionUtils.doWithFields(entityClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                // Find the semantics for this field
                final List<FieldSemantics> fieldSemanticsList = registry.getFieldSemantics(field);
                if (LOG.isTraceEnabled() && !fieldSemanticsList.isEmpty()) {
                    LOG.trace("field: {}", field);
                }

                // Try getting data using each of the field semantics in order
                for (FieldSemantics fieldSemantics : fieldSemanticsList) {
                    // Find the matching semantic field
                    final SemanticField semanticField = semanticFields.get(fieldSemantics);
                    if (LOG.isTraceEnabled() && semanticField != null) {
                        LOG.trace("fieldSemantics: {}, semanticField: {}", fieldSemantics, semanticField);
                    }

                    // TODO: Set propertyData in entity
                    // TODO: Handle special semantics such as propertyName = "_self" or "_all" (here or somewhere else?)
                    // See [C#] DD4TModelBuilder.CreateModelFromMapData

                    if (semanticField != null) {
                        Object fieldData = null;
                        try {
                            fieldData = fieldDataProvider.getFieldData(semanticField, new TypeDescriptor(field));
                        } catch (SemanticMappingException e) {
                            LOG.error("Exception while getting field data for: " + field, e);
                        }

                        if (fieldData != null) {
                            field.setAccessible(true);
                            field.set(entity, fieldData);
                            return;
                        }
                    }
                }
            }
        });

        LOG.trace("entity: {}", entity);
        return entity;
    }

    private Entity createInstance(Class<? extends Entity> entityClass) throws SemanticMappingException {
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
}

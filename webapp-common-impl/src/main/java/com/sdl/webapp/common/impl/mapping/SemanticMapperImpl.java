package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@Component
public class SemanticMapperImpl implements SemanticMapper {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticMapperImpl.class);

    private final SemanticMappingRegistry registry = new SemanticMappingRegistry();

    public SemanticMapperImpl() throws SemanticMappingException {
        this.registry.registerEntities(AbstractEntity.class.getPackage().getName());
    }

    @Override
    public Entity createEntity(Class<? extends Entity> entityClass, final SemanticSchema schema,
                               final SemanticFieldDataProvider fieldDataProvider) throws SemanticMappingException {
        final Entity entity = createInstance(entityClass);

        LOG.trace("schema: {}", schema.getId());
        ReflectionUtils.doWithFields(entityClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                LOG.trace("field: {}", field);
                for (FieldSemantics fieldSemantics : registry.getFieldSemantics(field)) {
                    final SemanticField semanticField = schema.getSemanticFields().get(fieldSemantics);
                    LOG.trace("fieldSemantics: {}, semanticField: {}", fieldSemantics, semanticField);

                    // TODO: Handle special semantics such as propertyName = "_self" (here or somewhere else?)

                    if (semanticField != null) {
                        final Object fieldData = fieldDataProvider.getFieldData(semanticField, new TypeDescriptor(field));
                        if (fieldData != null) {
                            field.setAccessible(true);
                            field.set(entity, fieldData);
                            return;
                        }
                    }
                }
            }
        });

        return entity;
    }

    private Entity createInstance(Class<? extends Entity> entityClass) throws SemanticMappingException {
        LOG.trace("entityClass: {}", entityClass);
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SemanticMappingException("Exception while creating instance of entity class: " +
                    entityClass.getName(), e);
        }
    }
}

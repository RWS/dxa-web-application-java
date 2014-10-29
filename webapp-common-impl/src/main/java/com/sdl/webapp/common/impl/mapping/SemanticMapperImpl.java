package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.config.SemanticSchemaField;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

// @Component -- NOTE: Disabled!
public class SemanticMapperImpl implements SemanticMapper {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticMapperImpl.class);

    private final WebRequestContext webRequestContext;

    private final SemanticInfoRegistry semanticInfoRegistry = new SemanticInfoRegistry();

    @Autowired
    public SemanticMapperImpl(WebRequestContext webRequestContext) throws SemanticMappingException {
        this.webRequestContext = webRequestContext;
        this.semanticInfoRegistry.registerEntities(AbstractEntity.class.getPackage().getName());
    }

    // TODO: Stuff in package ...common.mapping.config has to be organized differently so that this is not so
    // difficult and more efficient, and we don't need the WebRequestContext and the Localization here.

    @Override
    public Entity createEntity(Class<? extends Entity> entityClass, final SemanticSchema schema,
                               final SemanticFieldDataProvider fieldDataProvider) throws SemanticMappingException {
        final Localization localization = webRequestContext.getLocalization();

        final Entity entity = createInstance(entityClass);

        final List<SemanticEntityInfo> entityInfoList = semanticInfoRegistry.getEntityInfo(entityClass);

        ReflectionUtils.doWithFields(entityClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                for (SemanticEntityInfo entityInfo : entityInfoList) {
                    final String vocabulary = entityInfo.getVocabulary();
                    final String entityName = entityInfo.getEntityName();
                    LOG.debug("vocabulary={}, entityName={}", vocabulary, entityName);

                    for (SemanticPropertyInfo propertyInfo : entityInfo.getPropertyInfo()) {
                        final String propertyName = propertyInfo.getPropertyName();
                        LOG.debug("propertyName={}", propertyName);

                        if (propertyInfo.getField().equals(field)) {
                            if (vocabulary.equals(SemanticInfoRegistry.DEFAULT_VOCABULARY) ||
                                    schema.hasSchemaSemantics(vocabulary, entityName, localization)) {

                                final SemanticSchemaField schemaField = schema.findFieldBySemantics(vocabulary,
                                        entityName, propertyName, localization);

                                final Object fieldData = fieldDataProvider.getFieldData(schemaField);

                                if (fieldData != null) {
                                    field.setAccessible(true);
                                    field.set(entity, fieldData);
                                    return;
                                }
                            } else {
                                LOG.debug("Skipping SemanticPropertyInfo, not in the default vocabulary and " +
                                        "no matching schema semantics: {}", propertyInfo);
                            }
                        }
                    }
                }
            }
        });

        return entity;
    }

    private Entity createInstance(Class<? extends Entity> entityClass) throws SemanticMappingException {
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SemanticMappingException("Exception while creating instance of entity class: " +
                    entityClass.getName(), e);
        }
    }

}

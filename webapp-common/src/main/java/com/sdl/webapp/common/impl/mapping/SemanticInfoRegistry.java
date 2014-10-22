package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.Vocabularies;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.util.PackageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Semantic information registry.
 */
class SemanticInfoRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticInfoRegistry.class);

    private static final String DEFAULT_VOCABULARY = Vocabularies.SDL_CORE;
    private static final String DEFAULT_PREFIX = "";

    private final Map<Class<? extends Entity>, Map<String, SemanticEntityInfo>> entityInfo = new HashMap<>();

    public void registerEntities(String basePackage) throws SemanticMappingException {
        LOG.debug("Registering entity classes in package: {}", basePackage);

        try {
            PackageUtils.doWithClasses(basePackage, new PackageUtils.ClassCallback<SemanticMappingException>() {
                @Override
                public void doWith(MetadataReader metadataReader) throws SemanticMappingException {
                    final ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    if (!classMetadata.isInterface()) {
                        final String className = classMetadata.getClassName();
                        final Class<?> class_;
                        try {
                            class_ = Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            throw new SemanticMappingException("Exception while loading class: " + className, e);
                        }
                        if (Entity.class.isAssignableFrom(class_)) {
                            registerEntity(class_.asSubclass(Entity.class));
                        }
                    }
                }
            });
        } catch (IOException e) {
            throw new SemanticMappingException("Exception while registering entities in package: " + basePackage, e);
        }
    }

    public void registerEntity(Class<? extends Entity> entityClass) throws SemanticMappingException {
        LOG.debug("Registering entity class: {}", entityClass.getName());
        final Map<String, SemanticEntityInfo> semanticEntityInfo = createSemanticEntityInfo(entityClass);

        // Add default information if not present for the default prefix
        if (!semanticEntityInfo.containsKey(DEFAULT_PREFIX)) {
            semanticEntityInfo.put(DEFAULT_PREFIX, new SemanticEntityInfo("", DEFAULT_VOCABULARY, DEFAULT_PREFIX, false));
        }

        // Add information about properties
        for (Field field : entityClass.getDeclaredFields()) {
            for (SemanticPropertyInfo propertyInfo : createSemanticPropertyInfo(field)) {
                final SemanticEntityInfo matchingEntityInfo = semanticEntityInfo.get(propertyInfo.getPrefix());
                if (matchingEntityInfo != null) {
                    matchingEntityInfo.addPropertyInfo(propertyInfo);
                } else {
                    throw new SemanticMappingException("Semantic property information for the field '" +
                            propertyInfo.getField() + "' cannot be registered because it uses a prefix for which " +
                            "there is no semantic entity information. Make sure that the prefix is correct or that " +
                            "the class has a @SemanticEntity annotation for the prefix '" + propertyInfo.getPrefix() +
                            "'.");
                }
            }
        }

        entityInfo.put(entityClass, semanticEntityInfo);
    }

    private Map<String, SemanticEntityInfo> createSemanticEntityInfo(Class<? extends Entity> entityClass) {
        final Map<String, SemanticEntityInfo> result = new HashMap<>();

        final SemanticEntities semanticEntities = entityClass.getAnnotation(SemanticEntities.class);
        if (semanticEntities != null) {
            for (SemanticEntity semanticEntity : semanticEntities.value()) {
                result.put(semanticEntity.prefix(), new SemanticEntityInfo(semanticEntity));
            }
        } else {
            final SemanticEntity semanticEntity = entityClass.getAnnotation(SemanticEntity.class);
            if (semanticEntity != null) {
                result.put(semanticEntity.prefix(), new SemanticEntityInfo(semanticEntity));
            }
        }

        return result;
    }

    private List<SemanticPropertyInfo> createSemanticPropertyInfo(Field field) {
        final List<SemanticPropertyInfo> result = new ArrayList<>();

        final SemanticProperties semanticProperties = field.getAnnotation(SemanticProperties.class);
        if (semanticProperties != null) {
            for (SemanticProperty semanticProperty : semanticProperties.value()) {
                if (!semanticProperty.ignoreMapping()) {
                    result.add(new SemanticPropertyInfo(semanticProperty, field));
                } else {
                    LOG.debug("Skipping field because it is set to ignored: {}", field);
                }
            }
        } else {
            final SemanticProperty semanticProperty = field.getAnnotation(SemanticProperty.class);
            if (semanticProperty != null) {
                if (!semanticProperty.ignoreMapping()) {
                    result.add(new SemanticPropertyInfo(semanticProperty, field));
                } else {
                    LOG.debug("Skipping field because it is set to ignored: {}", field);
                }
            } else {
                final SemanticPropertyInfo propertyInfo = new SemanticPropertyInfo(DEFAULT_PREFIX, field.getName(), field);
                LOG.debug("Field {} has no semantic property annotation, creating from defaults: {}", field, propertyInfo);
                result.add(propertyInfo);
            }
        }

        return result;
    }

    public Map<Class<? extends Entity>, Map<String, SemanticEntityInfo>> getEntityInfo() {
        return entityInfo;
    }

    public Map<String, SemanticEntityInfo> getEntityInfo(Class<? extends Entity> entityClass) {
        return getEntityInfo().get(entityClass);
    }

    public SemanticEntityInfo getEntityInfo(Class<? extends Entity> entityClass, String prefix) {
        final Map<String, SemanticEntityInfo> map = getEntityInfo(entityClass);
        return map != null ? map.get(prefix) : null;
    }
}

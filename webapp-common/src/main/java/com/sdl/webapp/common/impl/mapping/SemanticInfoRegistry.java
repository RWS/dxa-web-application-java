package com.sdl.webapp.common.impl.mapping;

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
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class SemanticInfoRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticInfoRegistry.class);

    private static final String DEFAULT_VOCABULARY = Vocabularies.SDL_CORE;
    private static final String DEFAULT_PREFIX = "";

    private final Map<Class<? extends Entity>, Map<String, SemanticEntityInfo>> entityInfo = new HashMap<>();

    public void registerEntities(String basePackage) throws IOException {
        LOG.debug("Registering entity classes in package: {}", basePackage);

        PackageUtils.doWithClasses(basePackage, new PackageUtils.ClassCallback() {
            @Override
            public void doWith(MetadataReader metadataReader) {
                final ClassMetadata classMetadata = metadataReader.getClassMetadata();
                if (classMetadata.isConcrete()) {
                    final String className = classMetadata.getClassName();
                    try {
                        Class<?> cls = Class.forName(className);
                        if (Entity.class.isAssignableFrom(cls)) {
                            registerEntity(cls.asSubclass(Entity.class));
                        }
                    } catch (ClassNotFoundException e) {
                        LOG.error("Exception while loading class: {}", className, e);
                    }
                }
            }
        });
    }

    public void registerEntity(final Class<? extends Entity> entityClass) {
        LOG.debug("Registering entity class: {}", entityClass.getName());
        final Map<String, SemanticEntityInfo> semanticEntityInfo = createAllSemanticEntityInfo(entityClass);

        // Add default information if not present for the default prefix
        if (!semanticEntityInfo.containsKey(DEFAULT_PREFIX)) {
            semanticEntityInfo.put(DEFAULT_PREFIX, new SemanticEntityInfo("", DEFAULT_VOCABULARY, DEFAULT_PREFIX, false));
        }

        // Add information about properties
        ReflectionUtils.doWithFields(entityClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                for (SemanticPropertyInfo propertyInfo : createSemanticPropertyInfo(field)) {
                    final SemanticEntityInfo matchingEntityInfo = semanticEntityInfo.get(propertyInfo.getPrefix());
                    if (matchingEntityInfo != null) {
                        matchingEntityInfo.addPropertyInfo(propertyInfo);
                    }
                }
            }
        });

        entityInfo.put(entityClass, semanticEntityInfo);
    }

    private Map<String, SemanticEntityInfo> createAllSemanticEntityInfo(Class<?> class_) {
        final Map<String, SemanticEntityInfo> result = new HashMap<>();

        Class<?> cls = class_;
        while (cls != Object.class) {
            final Map<String, SemanticEntityInfo> semanticEntityInfo = createSemanticEntityInfo(cls);

            // Only add information if it is not yet defined for this prefix - definitions in subclasses must override
            // definitions in superclasses
            for (Map.Entry<String, SemanticEntityInfo> entry : semanticEntityInfo.entrySet()) {
                final String prefix = entry.getKey();
                if (!result.containsKey(prefix)) {
                    result.put(prefix, entry.getValue());
                } else {
                    LOG.warn("Semantic entity information in class {} with prefix '{}' is overridden in a subclass " +
                            "in the hierarchy of class {}", new Object[] { cls.getName(), prefix, class_.getName() });
                }
            }

            cls = cls.getSuperclass();
        }
        return result;
    }

    private Map<String, SemanticEntityInfo> createSemanticEntityInfo(Class<?> class_) {
        final SemanticEntities semanticEntities = class_.getAnnotation(SemanticEntities.class);
        if (semanticEntities != null) {
            final Map<String, SemanticEntityInfo> result = new HashMap<>();
            for (SemanticEntity semanticEntity : semanticEntities.value()) {
                result.put(semanticEntity.prefix(), new SemanticEntityInfo(semanticEntity));
            }
            return result;
        } else {
            final SemanticEntity semanticEntity = class_.getAnnotation(SemanticEntity.class);
            if (semanticEntity != null) {
                return Collections.singletonMap(semanticEntity.prefix(), new SemanticEntityInfo(semanticEntity));
            } else {
                return Collections.emptyMap();
            }
        }
    }

    private List<SemanticPropertyInfo> createSemanticPropertyInfo(Field field) {
        final SemanticProperties semanticProperties = field.getAnnotation(SemanticProperties.class);
        if (semanticProperties != null) {
            final List<SemanticPropertyInfo> result = new ArrayList<>();
            for (SemanticProperty semanticProperty : semanticProperties.value()) {
                if (!semanticProperty.ignoreMapping()) {
                    result.add(new SemanticPropertyInfo(semanticProperty, field));
                }
            }
            return result;
        } else {
            final SemanticProperty semanticProperty = field.getAnnotation(SemanticProperty.class);
            if (semanticProperty != null) {
                if (!semanticProperty.ignoreMapping()) {
                    return Collections.singletonList(new SemanticPropertyInfo(semanticProperty, field));
                } else {
                    return Collections.emptyList();
                }
            } else {
                final SemanticPropertyInfo propertyInfo = new SemanticPropertyInfo(DEFAULT_PREFIX, field.getName(), field);
                LOG.debug("Field {} has no semantic property annotation, creating from defaults: {}", field, propertyInfo);
                return Collections.singletonList(propertyInfo);
            }
        }
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

package com.sdl.webapp.common.impl.mapping2;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.mapping.annotations.*;
import com.sdl.webapp.common.api.mapping2.SemanticMappingException;
import com.sdl.webapp.common.api.mapping2.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping2.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.util.PackageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SemanticMappingRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticMappingRegistry.class);

    private final ListMultimap<Field, FieldSemantics> registry = ArrayListMultimap.create();

    public List<FieldSemantics> getFieldSemantics(Field field) {
        return registry.get(field);
    }

    public void registerEntities(String basePackage) throws SemanticMappingException {
        LOG.debug("Registering entity classes in package: {}", basePackage);

        try {
            PackageUtils.doWithClasses(basePackage, new PackageUtils.ClassCallback<SemanticMappingException>() {
                @Override
                public void doWith(MetadataReader metadataReader) throws SemanticMappingException {
                    final ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    if (!classMetadata.isInterface()) {
                        final Class<?> class_ = ClassUtils.resolveClassName(classMetadata.getClassName(),
                                ClassUtils.getDefaultClassLoader());
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
        if (entityClass.getAnnotation(SemanticMappingIgnore.class) != null) {
            LOG.debug("Ignoring entity class: {}", entityClass);
            return;
        }

        LOG.debug("Registering entity class: {}", entityClass.getName());

        final Map<String, SemanticEntityInfo> entityInfoMap = getSemanticEntityInfo(entityClass);
        final Map<String, SemanticVocabulary> vocabularies = new HashMap<>();

        for (Field field : entityClass.getDeclaredFields()) {
            final ListMultimap<String, SemanticPropertyInfo> propertyInfoMap = getSemanticPropertyInfo(field);

            for (Map.Entry<String, SemanticPropertyInfo> entry : propertyInfoMap.entries()) {
                final String prefix = entry.getKey();
                final SemanticEntityInfo entityInfo = entityInfoMap.get(prefix);
                if (entityInfo == null) {
                    throw new SemanticMappingException("The field " + field + " has a @SemanticProperty annotation " +
                            "with prefix '" + prefix + "', but the entity class has no @SemanticEntity annotation " +
                            "with this prefix.");
                }

                final String vocabularyId = entityInfo.getVocabulary();
                if (!vocabularies.containsKey(vocabularyId)) {
                    vocabularies.put(vocabularyId, new SemanticVocabulary(vocabularyId));
                }

                final FieldSemantics fieldSemantics = new FieldSemantics(vocabularies.get(vocabularyId),
                        entityInfo.getEntityName(), entry.getValue().getPropertyName());
                LOG.trace("FieldSemantics: {} -> {}", field, fieldSemantics);
                registry.put(field, fieldSemantics);
            }
        }
    }

    private Map<String, SemanticEntityInfo> getSemanticEntityInfo(Class<? extends Entity> entityClass)
            throws SemanticMappingException {
        // NOTE: LinkedHashMap because order of entries is important
        final Map<String, SemanticEntityInfo> result = new LinkedHashMap<>();

        final SemanticEntities wrapper = entityClass.getAnnotation(SemanticEntities.class);
        if (wrapper != null) {
            for (SemanticEntity annotation : wrapper.value()) {
                final SemanticEntityInfo entityInfo = new SemanticEntityInfo(annotation, entityClass);
                final String prefix = entityInfo.getPrefix();
                if (result.containsKey(prefix)) {
                    throw new SemanticMappingException("The entity class " + entityClass.getName() + " has multiple " +
                            "@SemanticEntity annotations with the same prefix '" + prefix + "'.");
                }

                result.put(prefix, entityInfo);
            }
        } else {
            final SemanticEntity annotation = entityClass.getAnnotation(SemanticEntity.class);
            if (annotation != null) {
                final SemanticEntityInfo entityInfo = new SemanticEntityInfo(annotation, entityClass);
                result.put(entityInfo.getPrefix(), entityInfo);
            }
        }

        // Add information for the default prefix if it was not specified explicitly
        if (!result.containsKey(SemanticEntityInfo.DEFAULT_PREFIX)) {
            result.put(SemanticEntityInfo.DEFAULT_PREFIX, new SemanticEntityInfo(entityClass));
        }

        LOG.trace("SemanticEntityInfo: {} -> {}", entityClass.getSimpleName(), result);
        return result;
    }

    private ListMultimap<String, SemanticPropertyInfo> getSemanticPropertyInfo(Field field) {
        // NOTE: LinkedListMultimap because order of entries is important
        final ListMultimap<String, SemanticPropertyInfo> result = LinkedListMultimap.create();

        final SemanticProperties wrapper = field.getAnnotation(SemanticProperties.class);
        if (wrapper != null) {
            for (SemanticProperty annotation : wrapper.value()) {
                final SemanticPropertyInfo propertyInfo = new SemanticPropertyInfo(annotation, field);
                result.put(propertyInfo.getPrefix(), propertyInfo);
            }
        } else {
            final SemanticProperty annotation = field.getAnnotation(SemanticProperty.class);
            if (annotation != null) {
                final SemanticPropertyInfo propertyInfo = new SemanticPropertyInfo(annotation, field);
                result.put(propertyInfo.getPrefix(), propertyInfo);
            }
        }

        // Add information for the default prefix if it was not specified explicitly
        if (!result.containsKey(SemanticEntityInfo.DEFAULT_PREFIX)) {
            result.put(SemanticEntityInfo.DEFAULT_PREFIX, new SemanticPropertyInfo(field));
        }

        LOG.trace("SemanticPropertyInfo: {} -> {}", field, result);
        return result;
    }
}

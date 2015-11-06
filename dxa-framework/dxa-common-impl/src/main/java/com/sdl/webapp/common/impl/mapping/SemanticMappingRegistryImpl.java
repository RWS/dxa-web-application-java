package com.sdl.webapp.common.impl.mapping;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.util.PackageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SemanticMappingRegistryImpl implements SemanticMappingRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticMappingRegistryImpl.class);

    private final SetMultimap<Field, FieldSemantics> fieldSemanticsMap = LinkedHashMultimap.create();
    private final SetMultimap<Class<? extends EntityModel>, SemanticEntityInfo> semanticEntityInfo = LinkedHashMultimap.create();
    private final SetMultimap<Field, SemanticPropertyInfo> semanticPropertyInfo = LinkedHashMultimap.create();

    @Override
    public Set<FieldSemantics> getFieldSemantics(Field field) {
        final Set<FieldSemantics> fieldSemanticsList = fieldSemanticsMap.get(field);
        return fieldSemanticsList != null ? fieldSemanticsList : Collections.<FieldSemantics>emptySet();
    }

    @Override
    public Set<SemanticEntityInfo> getEntityInfo(Class<? extends EntityModel> entityClass) {
        final Set<SemanticEntityInfo> result = new HashSet<>();

        // Get semantic entity info of this class and all superclasses (that implement interface Entity)
        Class<? extends EntityModel> cls = entityClass;
        while (cls != null) {
            result.addAll(semanticEntityInfo.get(cls));
            Class<?> superclass = cls.getSuperclass();
            cls = EntityModel.class.isAssignableFrom(superclass) ? superclass.asSubclass(EntityModel.class) : null;
        }

        return result;
    }

    @Override
    public Set<SemanticPropertyInfo> getPropertyInfo(Field field) {
        return semanticPropertyInfo.get(field);
    }

    @Override
    public void registerEntities(String basePackage) {
        LOG.debug("Registering entity classes in package: {}", basePackage);

        try {
            PackageUtils.doWithClasses(basePackage, new PackageUtils.ClassCallback() {
                @Override
                public void doWith(MetadataReader metadataReader) {
                    final ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    if (!classMetadata.isInterface()) {
                        final Class<?> class_ = ClassUtils.resolveClassName(classMetadata.getClassName(),
                                ClassUtils.getDefaultClassLoader());
                        if (EntityModel.class.isAssignableFrom(class_)) {
                            registerEntity(class_.asSubclass(EntityModel.class));
                        }
                    }
                }
            });
        } catch (IOException e) {
            // This means a class file could not be read; this should normally never happen
            throw new IllegalStateException("Exception while registering entities in package: " + basePackage, e);
        }
    }

    @Override
    public void registerEntity(Class<? extends EntityModel> entityClass) {
        // Ignore classes that have a @SemanticMappingIgnore annotation
        if (entityClass.getAnnotation(SemanticMappingIgnore.class) != null) {
            LOG.debug("Ignoring entity class: {}", entityClass);
            return;
        }

        if (semanticEntityInfo.containsKey(entityClass)) {
            LOG.debug("Entity class {} is already registered, ignoring", entityClass.getName());
            return;
        }


        LOG.debug("Registering entity class: {}", entityClass.getName());

        final Map<String, SemanticEntityInfo> entityInfoMap = createSemanticEntityInfo(entityClass);

        semanticEntityInfo.putAll(entityClass, entityInfoMap.values());

        final Map<String, SemanticVocabulary> vocabularies = new HashMap<>();

        for (Field field : this.getDeclaredFields(entityClass)) {
            final ListMultimap<String, SemanticPropertyInfo> propertyInfoMap = createSemanticPropertyInfo(field);
            semanticPropertyInfo.putAll(field, propertyInfoMap.values());

            for (Map.Entry<String, SemanticPropertyInfo> entry : propertyInfoMap.entries()) {
                // Get the corresponding semantic entity info
                final String prefix = entry.getKey();
                final SemanticEntityInfo entityInfo = entityInfoMap.get(prefix);
                if (entityInfo == null) {
                    throw new SemanticAnnotationException("The field " + field + " has a @SemanticProperty " +
                            "annotation with prefix '" + prefix + "', but the entity class has no @SemanticEntity " +
                            "annotation with this prefix.");
                }

                // Get the vocabulary id; create the vocabulary for this id if it is not yet created
                final String vocabularyId = entityInfo.getVocabulary();
                if (!vocabularies.containsKey(vocabularyId)) {
                    vocabularies.put(vocabularyId, new SemanticVocabulary(vocabularyId));
                }

                // Create the field semantics and store it
                final FieldSemantics fieldSemantics = new FieldSemantics(vocabularies.get(vocabularyId),
                        entityInfo.getEntityName(), entry.getValue().getPropertyName());
                LOG.trace("FieldSemantics: {} -> {}", field, fieldSemantics);
                fieldSemanticsMap.put(field, fieldSemantics);
            }
        }
    }

    @Override
    public Class<? extends EntityModel> getEntityClass(String entityName) {
        for (Class<? extends EntityModel> entityClass : semanticEntityInfo.keys()) {
            Set<SemanticEntityInfo> entityInfoList = semanticEntityInfo.get(entityClass);
            for (SemanticEntityInfo entityInfo : entityInfoList) {
                if (entityInfo.getEntityName().equals(entityName)) {
                    return entityClass;
                }
            }
        }
        return null;
    }

    @Override
    public Class<? extends EntityModel> getEntityClassByFullyQualifiedName(String entityName) {

        String[] entityNameSplit = entityName.split(":");
        List<Class<? extends EntityModel>> possibleValues = new ArrayList<>();
        for (Class<? extends EntityModel> entityClass : semanticEntityInfo.keys()) {
            Set<SemanticEntityInfo> entityInfoList = semanticEntityInfo.get(entityClass);
            for (SemanticEntityInfo entityInfo : entityInfoList) {
                if (entityName.startsWith(entityInfo.getVocabulary()) && entityName.endsWith(entityInfo.getEntityName())) {
                    if (!possibleValues.contains(entityClass)) {
                        possibleValues.add(entityClass);
                    }
                }
            }
        }
        if (possibleValues.size() > 0) {
            for (Class<? extends EntityModel> cls : possibleValues) {
                if (cls.getName().contains(entityNameSplit[entityNameSplit.length - 1])) {
                    return cls;
                }
            }
            return possibleValues.get(0);

        }
        return null;
    }

    /**
     * Get all declared fields. If concrete class is annotated as SemanticEntity, the whole inheritance structure is followed.
     *
     * @return list of class fields
     */
    private List<Field> getDeclaredFields(Class entityClass) {

        boolean followInheritanceStructure = true;
        if (entityClass.getAnnotation(SemanticEntity.class) == null) {
            followInheritanceStructure = false;
        }
        List<Field> declaredFields = new ArrayList<>();
        Class clazz = entityClass;
        while (!clazz.equals(AbstractEntityModel.class) && !clazz.equals(Object.class)) {
            Collections.addAll(declaredFields, clazz.getDeclaredFields());
            if (!followInheritanceStructure) break;
            clazz = clazz.getSuperclass();
        }
        return declaredFields;
    }

    /**
     * Creates semantic entity information for an entity class from the semantic annotations on the class.
     *
     * @param entityClass The entity class.
     * @return A map with {@code SemanticEntityInfo} objects by vocabulary prefix.
     */
    private Map<String, SemanticEntityInfo> createSemanticEntityInfo(Class<? extends EntityModel> entityClass) {
        // NOTE: LinkedHashMap because order of entries is important
        final Map<String, SemanticEntityInfo> result = new LinkedHashMap<>();

        final SemanticEntities wrapper = entityClass.getAnnotation(SemanticEntities.class);
        if (wrapper != null) {
            for (SemanticEntity annotation : wrapper.value()) {
                final SemanticEntityInfo entityInfo = new SemanticEntityInfo(annotation, entityClass);
                final String prefix = entityInfo.getPrefix();
                if (result.containsKey(prefix)) {
                    throw new SemanticAnnotationException("The entity class " + entityClass.getName() +
                            " has multiple @SemanticEntity annotations with the same prefix '" + prefix + "'.");
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

    /**
     * Creates semantic property information for a field from the semantic annotations on the field.
     *
     * @param field The field.
     * @return A map with {@code SemanticPropertyInfo} objects by vocabulary prefix.
     */
    private ListMultimap<String, SemanticPropertyInfo> createSemanticPropertyInfo(Field field) {
        // Ignore fields that have a @SemanticMappingIgnore annotation and static fields
        if (field.getAnnotation(SemanticMappingIgnore.class) != null || Modifier.isStatic(field.getModifiers())) {
            LOG.debug("Ignoring field: {}", field);
            return LinkedListMultimap.create();
        }

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

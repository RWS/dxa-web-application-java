package com.sdl.webapp.common.impl.mapping;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.util.PackageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class SemanticMappingRegistryImpl implements SemanticMappingRegistry {

    private final SetMultimap<Field, FieldSemantics> fieldSemanticsMap = LinkedHashMultimap.create();

    private final SetMultimap<Class<? extends EntityModel>, SemanticEntityInfo> semanticEntityInfo = LinkedHashMultimap.create();

    private final SetMultimap<Field, SemanticPropertyInfo> semanticPropertyInfo = LinkedHashMultimap.create();

    /**
     * Creates semantic entity information for an entity class from the semantic annotations on the class.
     *
     * @param entityClass The entity class.
     * @return A map with {@link SemanticEntityInfo} objects by vocabulary prefix.
     */
    private static Map<String, SemanticEntityInfo> createSemanticEntityInfo(Class<? extends EntityModel> entityClass) {
        // NOTE: LinkedHashMap because order of entries is important
        final Map<String, SemanticEntityInfo> result = new LinkedHashMap<>();

        final SemanticEntities wrapper = entityClass.getAnnotation(SemanticEntities.class);
        if (wrapper != null) {
            for (SemanticEntity annotation : wrapper.value()) {
                addEntityInfo(annotation, result, entityClass);
            }
        } else {
            addEntityInfo(entityClass.getAnnotation(SemanticEntity.class), result, entityClass);
        }

        // Add information for the default prefix if it was not specified explicitly
        if (!result.containsKey(SemanticEntityInfo.DEFAULT_PREFIX)) {
            result.put(SemanticEntityInfo.DEFAULT_PREFIX, new SemanticEntityInfo(entityClass));
        }

        log.trace("SemanticEntityInfo: {} -> {}", entityClass.getSimpleName(), result);
        return result;
    }

    /**
     * Get all declared fields. If concrete class is annotated as SemanticEntity, the whole inheritance structure is followed.
     *
     * @return list of class fields
     */
    private static List<Field> getDeclaredFields(Class<?> entityClass) {

        boolean followInheritanceStructure = entityClass.getAnnotation(SemanticEntity.class) != null;

        List<Field> declaredFields = new ArrayList<>();

        while (!entityClass.equals(AbstractEntityModel.class) && !entityClass.equals(Object.class)) {
            Collections.addAll(declaredFields, entityClass.getDeclaredFields());
            if (!followInheritanceStructure) {
                break;
            }
            entityClass = entityClass.getSuperclass();
        }
        return declaredFields;
    }

    /**
     * Creates semantic property information for a field from the semantic annotations on the field.
     *
     * @param field The field.
     * @return A map with {@code SemanticPropertyInfo} objects by vocabulary prefix.
     */
    private static ListMultimap<String, SemanticPropertyInfo> createSemanticPropertyInfo(Field field) {
        // Ignore fields that have a @SemanticMappingIgnore annotation and static fields
        if (field.getAnnotation(SemanticMappingIgnore.class) != null || Modifier.isStatic(field.getModifiers())) {
            log.debug("Ignoring field: {}", field);
            return LinkedListMultimap.create();
        }

        // NOTE: LinkedListMultimap because order of entries is important
        final ListMultimap<String, SemanticPropertyInfo> result = LinkedListMultimap.create();

        final SemanticProperties wrapper = field.getAnnotation(SemanticProperties.class);
        if (wrapper != null) {
            for (SemanticProperty annotation : wrapper.value()) {
                addPropertyInfo(annotation, result, field);
            }
        } else {
            addPropertyInfo(field.getAnnotation(SemanticProperty.class), result, field);
        }

        // Add information for the default prefix if it was not specified explicitly
        if (!result.containsKey(SemanticEntityInfo.DEFAULT_PREFIX)) {
            result.put(SemanticEntityInfo.DEFAULT_PREFIX, new SemanticPropertyInfo(field));
        }

        log.trace("SemanticPropertyInfo: {} -> {}", field, result);
        return result;
    }

    private static void addEntityInfo(SemanticEntity annotation, Map<String, SemanticEntityInfo> result, Class<? extends EntityModel> entityClass) {
        if (annotation != null) {
            final SemanticEntityInfo entityInfo = new SemanticEntityInfo(annotation, entityClass);
            final String prefix = entityInfo.getPrefix();
            if (result.containsKey(prefix)) {
                throw new SemanticAnnotationException("The entity class " + entityClass.getName() +
                        " has multiple @SemanticEntity annotations with the same prefix '" + prefix + "'.");
            }
            result.put(prefix, entityInfo);
        }
    }

    private static void addPropertyInfo(SemanticProperty annotation, Multimap<String, SemanticPropertyInfo> result, Field field) {
        if (annotation != null) {
            final SemanticPropertyInfo propertyInfo = new SemanticPropertyInfo(annotation, field);
            result.put(propertyInfo.getPrefix(), propertyInfo);
        }
    }

    @PostConstruct
    public void init() {
        log.debug("Auto registration of all static or top-level implementors of EntityModel class in packages");

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(EntityModel.class));

        Set<String> packages = new HashSet<>();

        for (String basePackage : Arrays.asList("com.sdl.dxa", "com.sdl.webapp")) {
            log.debug("Scanning {} for EntityModels", basePackage);
            for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
                String packageName = bd.getBeanClassName().substring(0, bd.getBeanClassName().lastIndexOf('.'));

                if (packages.add(packageName)) {
                    log.debug("Added package {} while scanning base package {}", packageName, basePackage);
                }
            }
        }

        for (String packageName : packages) {
            registerEntities(packageName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FieldSemantics> getFieldSemantics(Field field) {
        return fieldSemanticsMap.containsKey(field) ? fieldSemanticsMap.get(field) : Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<SemanticPropertyInfo> getPropertyInfo(Field field) {
        return semanticPropertyInfo.get(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerEntities(String basePackage) {
        log.debug("Registering entity classes in package: {}", basePackage);

        try {
            PackageUtils.doWithClasses(basePackage, metadataReader -> {
                final ClassMetadata classMetadata = metadataReader.getClassMetadata();
                if (!classMetadata.isInterface()) {
                    final Class<?> _class = ClassUtils.resolveClassName(classMetadata.getClassName(),
                            ClassUtils.getDefaultClassLoader());
                    if (EntityModel.class.isAssignableFrom(_class)) {
                        registerEntity(_class.asSubclass(EntityModel.class));
                    }
                }
            });
        } catch (IOException e) {
            // This means a class file could not be read; this should normally never happen
            throw new IllegalStateException("Exception while registering entities in package: " + basePackage, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerEntity(Class<? extends EntityModel> entityClass) {
        // Ignore classes that have a @SemanticMappingIgnore annotation
        if (entityClass.getAnnotation(SemanticMappingIgnore.class) != null) {
            log.debug("Ignoring entity class: {}", entityClass);
            return;
        }

        if (semanticEntityInfo.containsKey(entityClass)) {
            log.debug("Entity class {} is already registered, ignoring", entityClass.getName());
            return;
        }


        log.debug("Registering entity class: {}", entityClass.getName());

        final Map<String, SemanticEntityInfo> entityInfoMap = createSemanticEntityInfo(entityClass);

        semanticEntityInfo.putAll(entityClass, entityInfoMap.values());

        final Map<String, SemanticVocabulary> vocabularies = new HashMap<>();

        for (Field field : SemanticMappingRegistryImpl.getDeclaredFields(entityClass)) {
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
                log.trace("FieldSemantics: {} -> {}", field, fieldSemantics);
                fieldSemanticsMap.put(field, fieldSemantics);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends EntityModel> getEntityClassByFullyQualifiedName(String fullyQualifiedName, Class<? extends EntityModel> expectedClass) throws SemanticMappingException {

        String shortName = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(':') + 1);
        List<Class<? extends EntityModel>> possibleValues = new ArrayList<>();
        for (Map.Entry<Class<? extends EntityModel>, SemanticEntityInfo> entry : semanticEntityInfo.entries()) {
            if (isPossibleMappedClass(expectedClass, fullyQualifiedName, entry)) {
                if (expectedClass == null && Objects.equals(entry.getKey().getSimpleName(), shortName)) {
                    log.debug("Expected class is not provided, but found entity info with exact match of a class name {}, consider single match {}", shortName, entry.getKey());
                    return entry.getKey();
                }
                possibleValues.add(entry.getKey());
            }
        }

        if (possibleValues.isEmpty()) {
            log.trace("Cannot find any view model type for {}", fullyQualifiedName);
            return null;
        }

        if (possibleValues.size() > 1) {
            throw new SemanticMappingException("Ambiguous semantic mapping for " + fullyQualifiedName + ", found these mappings: " + possibleValues);
        }

        return possibleValues.get(0);
    }

    private boolean isPossibleMappedClass(Class<? extends EntityModel> expectedClass, String fullyQualifiedName, Map.Entry<Class<? extends EntityModel>, SemanticEntityInfo> entry) {
        SemanticEntityInfo entityInfo = entry.getValue();

        return String.format("%s:%s", entityInfo.getVocabulary(), entityInfo.getEntityName()).equals(fullyQualifiedName) &&
                (expectedClass == null || expectedClass.isAssignableFrom(entry.getKey()));
    }
}

package com.sdl.webapp.common.impl.mapping;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.annotations.*;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.util.PackageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Implementation of {@code SemanticMappingRegistry}.
 */
public class SemanticMappingRegistryImpl implements SemanticMappingRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticMappingRegistryImpl.class);

    private final ListMultimap<Field, FieldSemantics> fieldSemanticsMap = ArrayListMultimap.create();

    private final ListMultimap<Class<? extends Entity>, SemanticEntityInfo> semanticEntityInfo = ArrayListMultimap.create();
    private final ListMultimap<Field, SemanticPropertyInfo> semanticPropertyInfo = ArrayListMultimap.create();

    @Override
    public List<FieldSemantics> getFieldSemantics(Field field) {
        final List<FieldSemantics> fieldSemanticsList = fieldSemanticsMap.get(field);
        return fieldSemanticsList != null ? fieldSemanticsList : Collections.<FieldSemantics>emptyList();
    }

    @Override
    public List<SemanticEntityInfo> getEntityInfo(Class<? extends Entity> entityClass) {
        final List<SemanticEntityInfo> result = new ArrayList<>();

        // Get semantic entity info of this class and all superclasses (that implement interface Entity)
        Class<? extends Entity> cls = entityClass;
        while (cls != null) {
            result.addAll(semanticEntityInfo.get(cls));
            Class<?> superclass = cls.getSuperclass();
            cls = Entity.class.isAssignableFrom(superclass) ? superclass.asSubclass(Entity.class) : null;
        }

        return result;
    }

    @Override
    public List<SemanticPropertyInfo> getPropertyInfo(Field field) {
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
                        if (Entity.class.isAssignableFrom(class_)) {
                            registerEntity(class_.asSubclass(Entity.class));
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
    public void registerEntity(Class<? extends Entity> entityClass) {
        // Ignore classes that have a @SemanticMappingIgnore annotation
        if (entityClass.getAnnotation(SemanticMappingIgnore.class) != null) {
            LOG.debug("Ignoring entity class: {}", entityClass);
            return;
        }

        LOG.debug("Registering entity class: {}", entityClass.getName());

        final Map<String, SemanticEntityInfo> entityInfoMap = createSemanticEntityInfo(entityClass);
        semanticEntityInfo.putAll(entityClass, entityInfoMap.values());

        final Map<String, SemanticVocabulary> vocabularies = new HashMap<>();

        for (Field field : this.getDeclaredFields(entityClass) ) {
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

    /**
     * Get all declared fields. If concrete class is annotated as SemanticEntity, the whole inheritance structure is followed.
     *
     * @param entityClass
     * @return list of class fields
     */
    private List<Field> getDeclaredFields(Class entityClass) {

        boolean followInheritanceStructure = true;
        if ( entityClass.getAnnotation(SemanticEntity.class) == null ) {
            followInheritanceStructure = false;
        }
        List<Field> declaredFields = new ArrayList<>();
        Class clazz = entityClass;
        while ( ! clazz.equals(AbstractEntity.class) && ! clazz.equals(Object.class) ) {
            for ( Field field : clazz.getDeclaredFields() ) {
                declaredFields.add(field);
            }
            if ( !followInheritanceStructure ) break;
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
    private Map<String, SemanticEntityInfo> createSemanticEntityInfo(Class<? extends Entity> entityClass) {
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

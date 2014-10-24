package com.sdl.webapp.common.impl.mapping;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
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
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Semantic information registry.
 *
 * This holds information about semantic mapping determined from semantic mapping annotations used on entity classes.
 */
final class SemanticInfoRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticInfoRegistry.class);

    public static final String DEFAULT_VOCABULARY = Vocabularies.SDL_CORE;
    public static final String DEFAULT_PREFIX = "";

    private final ListMultimap<Class<? extends Entity>, SemanticEntityInfo> entityInfo = ArrayListMultimap.create();

    /**
     * Register the entity classes in the specified package.
     *
     * @param basePackage The package to search for entity classes.
     * @throws SemanticMappingException If an error occurs while inspecting the classes, for example because the
     *      semantic mapping defined on a class is incorrect.
     */
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

    /**
     * Register an entity class.
     *
     * @param entityClass The entity class to register.
     * @throws SemanticMappingException If an error occurs while inspecting the class, for example because the semantic
     *      mapping defined on the class is incorrect.
     */
    public void registerEntity(Class<? extends Entity> entityClass) throws SemanticMappingException {
        LOG.debug("Registering entity class: {}", entityClass.getName());
        final Map<String, SemanticEntityInfo> semanticEntityInfo = createSemanticEntityInfo(entityClass);

        // Add default information if not present for the default prefix
        if (!semanticEntityInfo.containsKey(DEFAULT_PREFIX)) {
            semanticEntityInfo.put(DEFAULT_PREFIX, new SemanticEntityInfo(entityClass.getSimpleName(),
                    DEFAULT_VOCABULARY, false));
        }

        // Add information about properties
        for (Field field : entityClass.getDeclaredFields()) {
            for (Map.Entry<String, SemanticPropertyInfo> entry : createSemanticPropertyInfo(field).entries()) {
                final String prefix = entry.getKey();
                final SemanticPropertyInfo semanticPropertyInfo = entry.getValue();

                final SemanticEntityInfo matchingEntityInfo = semanticEntityInfo.get(prefix);
                if (matchingEntityInfo != null) {
                    matchingEntityInfo.addPropertyInfo(semanticPropertyInfo);
                } else {
                    throw new SemanticMappingException("Semantic property information for the field '" +
                            semanticPropertyInfo.getField() + "' cannot be registered because it uses a prefix for " +
                            "which there is no semantic entity information. Make sure that the prefix is correct or " +
                            "that the class has a @SemanticEntity annotation for the prefix '" + prefix + "'.");
                }
            }
        }

        entityInfo.putAll(entityClass, semanticEntityInfo.values());
    }

    /**
     * Creates semantic entity information for the specified class.
     *
     * @param entityClass The entity class.
     * @return A {@code Map} containing {@code SemanticEntityInfo} objects by vocabulary prefix.
     * @throws SemanticMappingException If an error occurs while inspecting the class, for example because the semantic
     *      mapping defined on the class is incorrect.
     */
    private Map<String, SemanticEntityInfo> createSemanticEntityInfo(Class<? extends Entity> entityClass)
            throws SemanticMappingException {
        final Map<String, SemanticEntityInfo> result = new HashMap<>();

        for (SemanticEntity semanticEntity : getSemanticEntityAnnotations(entityClass)) {
            final String prefix = semanticEntity.prefix();
            if (!result.containsKey(prefix)) {
                result.put(prefix, new SemanticEntityInfo(semanticEntity, entityClass));
            } else {
                throw new SemanticMappingException("There are multiple @SemanticEntity annotations on the class '" +
                        entityClass.getName() + "' with the same prefix '" + prefix + "'. Make sure that the " +
                        "prefixes are unique for each @SemanticEntity annotation on this class.");
            }
        }

        return result;
    }

    private List<SemanticEntity> getSemanticEntityAnnotations(AnnotatedElement annotatedElement) {
        final SemanticEntities wrapper = annotatedElement.getAnnotation(SemanticEntities.class);
        if (wrapper != null) {
            return Arrays.asList(wrapper.value());
        } else {
            final SemanticEntity annotation = annotatedElement.getAnnotation(SemanticEntity.class);
            if (annotation != null) {
                return Collections.singletonList(annotation);
            } else {
                return Collections.emptyList();
            }
        }
    }

    /**
     * Creates semantic property information for the specified field.
     *
     * @param field The field.
     * @return A {@code Map} containing {@code SemanticPropertyInfo} objects by vocabulary prefix.
     */
    private ListMultimap<String, SemanticPropertyInfo> createSemanticPropertyInfo(Field field) {
        final ListMultimap<String, SemanticPropertyInfo> result = ArrayListMultimap.create();

        final List<SemanticProperty> semanticProperties = getSemanticPropertyAnnotations(field);
        if (!semanticProperties.isEmpty()) {
            for (SemanticProperty semanticProperty : semanticProperties) {
                if (!semanticProperty.ignoreMapping()) {
                    String s = semanticProperty.propertyName();
                    if (Strings.isNullOrEmpty(s)) {
                        s = Strings.nullToEmpty(semanticProperty.value());
                    }
                    final int i = s.indexOf(':');
                    final String prefix = i > 0 ? s.substring(0, i) : DEFAULT_PREFIX;

                    result.put(prefix, new SemanticPropertyInfo(semanticProperty, field));
                } else {
                    LOG.debug("Skipping field because it is set to ignored: {}", field);
                }
            }
        } else {
            LOG.debug("Field {} has no semantic property annotation, creating default information", field);
            result.put(DEFAULT_PREFIX, new SemanticPropertyInfo(field.getName(), field));
        }

        return result;
    }

    private List<SemanticProperty> getSemanticPropertyAnnotations(AnnotatedElement annotatedElement) {
        final SemanticProperties wrapper = annotatedElement.getAnnotation(SemanticProperties.class);
        if (wrapper != null) {
            return Arrays.asList(wrapper.value());
        } else {
            final SemanticProperty annotation = annotatedElement.getAnnotation(SemanticProperty.class);
            if (annotation != null) {
                return Collections.singletonList(annotation);
            } else {
                return Collections.emptyList();
            }
        }
    }

    /**
     * Gets the semantic mapping information in this registry.
     *
     * @return A {@code ListMultimap} in which the keys are entity classes and the values are {@code SemanticEntityInfo}
     *      objects.
     */
    public ListMultimap<Class<? extends Entity>, SemanticEntityInfo> getEntityInfo() {
        return entityInfo;
    }

    /**
     * Gets the semantic mapping information for a specific entity class.
     *
     * @param entityClass The entity class.
     * @return A {@code List} of {@code SemanticEntityInfo} objects.
     */
    public List<SemanticEntityInfo> getEntityInfo(Class<? extends Entity> entityClass) {
        return entityInfo.get(entityClass);
    }
}

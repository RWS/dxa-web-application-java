package com.sdl.dxa.api.datamodel.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.dxa.api.datamodel.model.util.ListWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.type.TypeFactory.unknownType;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;

/**
 * The {@link TypeIdResolverBase} that handles DXA specific [type ID &lt;-&gt; class] logic.
 * <p>{@code SomeClass} maps basically to {@code com.sdl.dxa.model.data.SomeClass} and
 * {@code SomeClass[]} in turn to {@link ListWrapper}{@code <SomeClass>}.</p>
 * <p>In case a specific {@link ListWrapper} is implemented for
 * this class, it's being then used without type information on a leaves of the list.</p>
 * <p>Basic {@code java.lang.*} objects lists are also handled.</p>
 * <p>The mapping information is gotten from {@link JsonTypeName} annotation on classes inside the package of {@link ViewModelData}
 * or from basic information about the known class in annotation is not present.</p>
 */
@Slf4j
public class ModelDataTypeIdResolver extends TypeIdResolverBase {

    private static final String LIST_MARKER = "[]";

    private static final Map<String, JavaType> BASIC_MAPPING = new HashMap<>();

    static {
        Stream.of(Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class,
                Double.class, String.class).forEach(aClass -> {
            addMapping(aClass.getSimpleName(), aClass);
            addMapping(aClass.getSimpleName() + LIST_MARKER, ListWrapper.class, aClass);
        });

        Stream.of(Date.class, DateTime.class).forEach(aClass -> {
            addMapping(aClass.getSimpleName(), String.class);
            addMapping(aClass.getSimpleName() + LIST_MARKER, ListWrapper.class, String.class);
        });

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(JsonTypeName.class));
        scanner.findCandidateComponents(ViewModelData.class.getPackage().getName())
                .forEach(type -> {
                    try {
                        Class<?> aClass = forName(type.getBeanClassName(), getDefaultClassLoader());
                        JsonTypeName typeName = aClass.getAnnotation(JsonTypeName.class);
                        addMapping(defaultIfBlank(typeName.value(), aClass.getSimpleName()), aClass);
                    } catch (ClassNotFoundException e) {
                        log.warn("Class not found while mapping model data to typeIDs. Should never happen.", e);
                    }
                });
    }

    private static void addMapping(String classId, Class<?> basicClass) {
        addMapping(classId, basicClass, null);
    }

    private static void addMapping(String classId, Class<?> basicClass, Class<?> genericClass) {
        JavaType javaType = genericClass == null ?
                TypeFactory.defaultInstance().constructSpecializedType(unknownType(), basicClass) :
                TypeFactory.defaultInstance().constructParametricType(basicClass, genericClass);

        BASIC_MAPPING.put(classId, javaType);
        log.trace("Added mapping for polymorphic deserialization {} <-> {}", classId, javaType);
    }

    @Override
    public String idFromValue(Object value) {
        return getIdFromValue(value);
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return getIdFromValue(value);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @NotNull
    private String getIdFromValue(Object value) {
        if (value == null) {
            log.warn("Should normally never happen, Jackson should handle nulls");
            return "unknown";
        }

        JsonTypeName annotation = value.getClass().getAnnotation(JsonTypeName.class);
        if (annotation != null && !isEmpty(annotation.value())) {
            log.trace("Type ID for value '{}' taken from annotation and is '{}'", value, annotation.value());
            return annotation.value();
        }

        if (value instanceof ListWrapper && !((ListWrapper) value).empty()) {
            String simpleName = ((ListWrapper) value).get(0).getClass().getSimpleName();
            String id = getMappingName(simpleName) + LIST_MARKER;
            log.trace("Value is instance of ListWrapper without an explicit implementation, value = '{}', id = '{}'", id);
            return id;
        }

        String id = getMappingName(value.getClass().getSimpleName());
        log.trace("Value is unknown class without annotation, value = '{}', id = '{}'", id);
        return id;
    }

    private String getMappingName(String simpleName) {
        return BASIC_MAPPING.containsKey(simpleName) ? BASIC_MAPPING.get(simpleName).getRawClass().getSimpleName() : simpleName;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        JavaType javaType = BASIC_MAPPING.get(id);
        log.trace("Type ID '{}' is mapped to '{}'", id, javaType);
        return javaType;
    }
}

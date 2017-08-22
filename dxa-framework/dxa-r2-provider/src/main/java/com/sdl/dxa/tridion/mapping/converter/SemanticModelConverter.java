package com.sdl.dxa.tridion.mapping.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Interface used for converting complex values  while doing a semantic mapping with {@link SemanticFieldDataProvider}.
 */
public interface SemanticModelConverter<T> {

    static TypeInformation getTypeInformation(TypeDescriptor targetType) {
        Class<?> objectType = targetType.getObjectType();

        Class<? extends Collection> collectionType = null;

        if (Collection.class.isAssignableFrom(objectType)) {
            //typecast is safe because of if statement
            //noinspection unchecked
            collectionType = (Class<? extends Collection>) objectType;
            objectType = targetType.getElementTypeDescriptor().getObjectType();
        }

        return TypeInformation.builder()
                .objectType(objectType)
                .collectionType(collectionType)
                .build();
    }

    Object convert(T toConvert, TypeInformation targetType, SemanticField semanticField,
                   ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException;

    List<Class<? extends T>> getTypes();

    default Object convertToCollectionIfNeeded(Object value, TypeInformation typeInformation) {
        Class<? extends Collection> collectionType = typeInformation.getCollectionType();
        if (collectionType != null) {
            if (Set.class.isAssignableFrom(collectionType)) {
                return Sets.newHashSet(value);
            } else if (List.class.isAssignableFrom(collectionType)) {
                return Lists.newArrayList(value);
            }
        }
        return value;
    }

    @NotNull
    default Number toSpecificNumber(Number number, Class<?> objectType) {
        // conversion to string helps to prevent floating point accuracy issues
        Number _number = new BigDecimal(String.valueOf(number));
        if (Float.class == objectType) {
            return _number.floatValue();
        }
        if (Double.class == objectType) {
            return _number.doubleValue();
        }
        if (Integer.class == objectType) {
            return _number.intValue();
        }
        if (Long.class == objectType) {
            return _number.longValue();
        }
        if (Byte.class == objectType) {
            return _number.byteValue();
        }
        if (Short.class == objectType) {
            return _number.shortValue();
        }
        return _number;
    }
}

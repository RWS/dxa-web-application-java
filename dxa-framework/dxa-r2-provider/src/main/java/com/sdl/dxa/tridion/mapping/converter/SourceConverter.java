package com.sdl.dxa.tridion.mapping.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sdl.dxa.R2;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Interface used for converting complex values  while doing a semantic mapping with {@link SemanticFieldDataProvider}.
 */
@R2
public interface SourceConverter<T> {

    List<Class<? extends T>> getTypes();

    Object convert(T toConvert, TypeInformation targetType, SemanticField semanticField,
                   ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException;

    default Object wrapIfNeeded(Object value, TypeInformation typeInformation) {
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
}

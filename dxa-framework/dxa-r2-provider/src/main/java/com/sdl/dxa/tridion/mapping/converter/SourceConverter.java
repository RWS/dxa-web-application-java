package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

/**
 * Interface used for converting complex values  while doing a semantic mapping with {@link SemanticFieldDataProvider}.
 */
public interface SourceConverter<T> {

    List<Class<? extends T>> getTypes();

    Object convert(T toConvert, TypeDescriptor targetType, SemanticField semanticField, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException;
}

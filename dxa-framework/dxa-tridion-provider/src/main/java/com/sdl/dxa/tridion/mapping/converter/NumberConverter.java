package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class NumberConverter implements SemanticModelConverter<Number> {

    @Override
    public Object convert(Number toConvert, TypeInformation targetType, SemanticField semanticField, ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        Class<?> objectType = targetType.getObjectType();
        Number number = toSpecificNumber(toConvert, objectType);
        return convertToCollectionIfNeeded(number, targetType);
    }

    @Override
    public List<Class<? extends Number>> getTypes() {
        return Arrays.asList(Number.class, Float.class, Double.class, Byte.class, Integer.class, Long.class, Short.class);
    }
}

package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StringConverter implements SourceConverter<String> {

    @Override
    public List<Class<? extends String>> getTypes() {
        return Collections.singletonList(String.class);
    }

    @Override
    public Object convert(String toConvert, TypeDescriptor targetType, SemanticField semanticField, DefaultSemanticFieldDataProvider dataProvider) {
        Class<?> objectType = targetType.getObjectType();
        if (Date.class.isAssignableFrom(objectType)) {
            try {
                return DateFormat.getInstance().parse(toConvert);
            } catch (ParseException e) {
                log.warn("Cannot parse a date string {} to a date of class {}", toConvert, Date.class);
            }
        }

        if (Number.class.isAssignableFrom(objectType)) {
            return toNumber(toConvert, objectType);
        }

        if (Boolean.class == objectType) {
            return Boolean.parseBoolean(toConvert);
        }

        return toConvert;
    }

    @NotNull
    private Number toNumber(String toConvert, Class<?> objectType) {
        BigDecimal number = new BigDecimal(toConvert);
        if (Float.class == objectType) {
            return number.floatValue();
        }
        if (Double.class == objectType) {
            return number.doubleValue();
        }
        if (Integer.class == objectType) {
            return number.intValue();
        }
        if (Long.class == objectType) {
            return number.longValue();
        }
        if (Byte.class == objectType) {
            return number.byteValue();
        }
        if (Short.class == objectType) {
            return number.shortValue();
        }
        return number;
    }
}

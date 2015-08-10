package com.sdl.webapp.dd4t.fieldconverters;

import org.dd4t.contentmodel.FieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FieldConverterRegistry {

    private final Map<FieldType, FieldConverter> fieldConverters = new HashMap<>();

    @Autowired
    public FieldConverterRegistry(List<FieldConverter> fieldConverterList) {
        for (FieldConverter fieldConverter : fieldConverterList) {
            for (FieldType fieldType : fieldConverter.supportedFieldTypes()) {
                fieldConverters.put(fieldType, fieldConverter);
            }
        }
    }

    public FieldConverter getFieldConverterFor(FieldType fieldType) throws UnsupportedFieldTypeException {
        final FieldConverter fieldConverter = fieldConverters.get(fieldType);
        if (fieldConverter == null) {
            throw new UnsupportedFieldTypeException(fieldType);
        }

        return fieldConverter;
    }
}

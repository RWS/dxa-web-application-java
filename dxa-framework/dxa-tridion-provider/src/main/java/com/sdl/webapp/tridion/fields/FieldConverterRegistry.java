package com.sdl.webapp.tridion.fields;

import com.sdl.webapp.tridion.fields.converters.FieldConverter;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedFieldTypeException;
import org.dd4t.contentmodel.FieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
/**
 * <p>FieldConverterRegistry class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class FieldConverterRegistry {

    private final Map<FieldType, FieldConverter> fieldConverters = new HashMap<>();

    /**
     * <p>Constructor for FieldConverterRegistry.</p>
     *
     * @param fieldConverterList a {@link java.util.List} object.
     */
    @Autowired
    public FieldConverterRegistry(List<FieldConverter> fieldConverterList) {
        for (FieldConverter fieldConverter : fieldConverterList) {
            for (FieldType fieldType : fieldConverter.supportedFieldTypes()) {
                fieldConverters.put(fieldType, fieldConverter);
            }
        }
    }

    /**
     * <p>getFieldConverterFor.</p>
     *
     * @param fieldType a {@link org.dd4t.contentmodel.FieldType} object.
     * @return a {@link com.sdl.webapp.tridion.fields.converters.FieldConverter} object.
     * @throws com.sdl.webapp.tridion.fields.exceptions.UnsupportedFieldTypeException if any.
     */
    public FieldConverter getFieldConverterFor(FieldType fieldType) throws UnsupportedFieldTypeException {
        final FieldConverter fieldConverter = fieldConverters.get(fieldType);
        if (fieldConverter == null) {
            throw new UnsupportedFieldTypeException(fieldType);
        }

        return fieldConverter;
    }
}

package com.sdl.webapp.tridion.fieldconverters;

import org.dd4t.contentmodel.FieldType;

/**
 * Thrown by {@code FieldConverterRegistry} when there is no {@code FieldConverter} for the specified field type.
 */
public class UnsupportedFieldTypeException extends FieldConverterException {

    public UnsupportedFieldTypeException(FieldType fieldType) {
        super("No field converter available for field type: " + fieldType);
    }
}

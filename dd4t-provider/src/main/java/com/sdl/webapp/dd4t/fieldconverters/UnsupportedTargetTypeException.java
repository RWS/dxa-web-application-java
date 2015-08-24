package com.sdl.webapp.dd4t.fieldconverters;

import org.springframework.core.convert.TypeDescriptor;

/**
 * Thrown by a {@code FieldConverter} when the specified target type is not supported.
 */
public class UnsupportedTargetTypeException extends FieldConverterException {

    public UnsupportedTargetTypeException(TypeDescriptor targetType) {
        super("Unsupported target type: " + targetType);
    }

    public UnsupportedTargetTypeException(Class<?> targetClass) {
        super("Unsupported target class: " + targetClass.getName());
    }
}

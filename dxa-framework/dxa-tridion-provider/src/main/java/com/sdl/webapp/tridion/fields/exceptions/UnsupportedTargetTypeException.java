package com.sdl.webapp.tridion.fields.exceptions;

import org.springframework.core.convert.TypeDescriptor;

/**
 * Thrown by a {@code FieldConverter} when the specified target type is not supported.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class UnsupportedTargetTypeException extends FieldConverterException {

    /**
     * <p>Constructor for UnsupportedTargetTypeException.</p>
     *
     * @param targetType a {@link org.springframework.core.convert.TypeDescriptor} object.
     */
    public UnsupportedTargetTypeException(TypeDescriptor targetType) {
        super("Unsupported target type: " + targetType);
    }

    /**
     * <p>Constructor for UnsupportedTargetTypeException.</p>
     *
     * @param targetClass a {@link java.lang.Class} object.
     */
    public UnsupportedTargetTypeException(Class<?> targetClass) {
        super("Unsupported target class: " + targetClass.getName());
    }
}

package com.sdl.webapp.tridion.fields.exceptions;

import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;

/**
 * <p>FieldConverterException class.</p>
 */
public class FieldConverterException extends SemanticMappingException {

    /**
     * <p>Constructor for FieldConverterException.</p>
     */
    public FieldConverterException() {
    }

    /**
     * <p>Constructor for FieldConverterException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public FieldConverterException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for FieldConverterException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public FieldConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for FieldConverterException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public FieldConverterException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for FieldConverterException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public FieldConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

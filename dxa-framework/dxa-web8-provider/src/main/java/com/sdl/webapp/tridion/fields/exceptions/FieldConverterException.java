package com.sdl.webapp.tridion.fields.exceptions;

import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;

public class FieldConverterException extends SemanticMappingException {

    public FieldConverterException() {
    }

    public FieldConverterException(String message) {
        super(message);
    }

    public FieldConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldConverterException(Throwable cause) {
        super(cause);
    }

    public FieldConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

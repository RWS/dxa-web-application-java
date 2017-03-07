package com.sdl.webapp.common.api.mapping.semantic;

import com.sdl.webapp.common.exceptions.DxaException;

public class SemanticMappingException extends DxaException {

    public SemanticMappingException() {
    }

    public SemanticMappingException(String message) {
        super(message);
    }

    public SemanticMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SemanticMappingException(Throwable cause) {
        super(cause);
    }

    public SemanticMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

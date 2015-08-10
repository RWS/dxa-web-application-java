package com.sdl.webapp.common.api.mapping;

public class SemanticMappingException extends Exception {

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

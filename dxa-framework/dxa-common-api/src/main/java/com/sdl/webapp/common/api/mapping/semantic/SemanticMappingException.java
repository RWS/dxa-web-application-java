package com.sdl.webapp.common.api.mapping.semantic;

/**
 * <p>SemanticMappingException class.</p>
 */
public class SemanticMappingException extends Exception {

    /**
     * <p>Constructor for SemanticMappingException.</p>
     */
    public SemanticMappingException() {
    }

    /**
     * <p>Constructor for SemanticMappingException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public SemanticMappingException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for SemanticMappingException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public SemanticMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for SemanticMappingException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public SemanticMappingException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for SemanticMappingException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public SemanticMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

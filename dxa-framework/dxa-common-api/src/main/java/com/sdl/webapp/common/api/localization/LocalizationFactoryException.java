package com.sdl.webapp.common.api.localization;

/**
 * Thrown when an error occurs in a localization factory while creating a localization.
 */
public class LocalizationFactoryException extends Exception {

    /**
     * <p>Constructor for LocalizationFactoryException.</p>
     */
    public LocalizationFactoryException() {
    }

    /**
     * <p>Constructor for LocalizationFactoryException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public LocalizationFactoryException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for LocalizationFactoryException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public LocalizationFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for LocalizationFactoryException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public LocalizationFactoryException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for LocalizationFactoryException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public LocalizationFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

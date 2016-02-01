package com.sdl.webapp.common.api.localization;

/**
 * Thrown when an error occurs in a localization resolver when resolving a localization.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class LocalizationResolverException extends Exception {

    /**
     * <p>Constructor for LocalizationResolverException.</p>
     */
    public LocalizationResolverException() {
    }

    /**
     * <p>Constructor for LocalizationResolverException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public LocalizationResolverException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for LocalizationResolverException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public LocalizationResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for LocalizationResolverException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public LocalizationResolverException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for LocalizationResolverException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public LocalizationResolverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

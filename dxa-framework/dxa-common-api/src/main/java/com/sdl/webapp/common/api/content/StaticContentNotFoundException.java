package com.sdl.webapp.common.api.content;

/**
 * Thrown when a static content provider cannot find the requested static content.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class StaticContentNotFoundException extends ContentProviderException {

    /**
     * <p>Constructor for StaticContentNotFoundException.</p>
     */
    public StaticContentNotFoundException() {
    }

    /**
     * <p>Constructor for StaticContentNotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public StaticContentNotFoundException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for StaticContentNotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public StaticContentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for StaticContentNotFoundException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public StaticContentNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for StaticContentNotFoundException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public StaticContentNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

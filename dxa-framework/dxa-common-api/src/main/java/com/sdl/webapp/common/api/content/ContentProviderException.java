package com.sdl.webapp.common.api.content;

/**
 * Thrown when an error occurs related to a content provider.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class ContentProviderException extends Exception {

    /**
     * <p>Constructor for ContentProviderException.</p>
     */
    public ContentProviderException() {
    }

    /**
     * <p>Constructor for ContentProviderException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public ContentProviderException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for ContentProviderException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public ContentProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for ContentProviderException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public ContentProviderException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for ContentProviderException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public ContentProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

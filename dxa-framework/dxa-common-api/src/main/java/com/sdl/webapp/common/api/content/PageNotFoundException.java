package com.sdl.webapp.common.api.content;

/**
 * Thrown when a content provider cannot find the requested page.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class PageNotFoundException extends ContentProviderException {

    /**
     * <p>Constructor for PageNotFoundException.</p>
     */
    public PageNotFoundException() {
    }

    /**
     * <p>Constructor for PageNotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public PageNotFoundException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for PageNotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public PageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for PageNotFoundException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public PageNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for PageNotFoundException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public PageNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

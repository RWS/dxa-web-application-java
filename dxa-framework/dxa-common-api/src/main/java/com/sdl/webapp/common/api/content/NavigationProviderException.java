package com.sdl.webapp.common.api.content;

/**
 * Thrown when an error occurs related to a navigation provider.
 */
public class NavigationProviderException extends ContentProviderException {

    /**
     * <p>Constructor for NavigationProviderException.</p>
     */
    public NavigationProviderException() {
    }

    /**
     * <p>Constructor for NavigationProviderException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public NavigationProviderException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for NavigationProviderException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public NavigationProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for NavigationProviderException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public NavigationProviderException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for NavigationProviderException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public NavigationProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

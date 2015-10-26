package com.sdl.webapp.common.api.content;

/**
 * Thrown when an error occurs related to a content provider.
 */
public class ContentProviderException extends Exception {

    public ContentProviderException() {
    }

    public ContentProviderException(String message) {
        super(message);
    }

    public ContentProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentProviderException(Throwable cause) {
        super(cause);
    }

    public ContentProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

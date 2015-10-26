package com.sdl.webapp.common.api.content;

/**
 * Thrown when a static content provider cannot find the requested static content.
 */
public class StaticContentNotFoundException extends ContentProviderException {

    public StaticContentNotFoundException() {
    }

    public StaticContentNotFoundException(String message) {
        super(message);
    }

    public StaticContentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaticContentNotFoundException(Throwable cause) {
        super(cause);
    }

    public StaticContentNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.sdl.webapp.common.api.content;

/**
 * Thrown when a static content provider cannot load the requested static content.
 * @dxa.publicApi
 */
public class StaticContentNotLoadedException extends ContentProviderException {

    public StaticContentNotLoadedException() {
    }

    public StaticContentNotLoadedException(String message) {
        super(message);
    }

    public StaticContentNotLoadedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaticContentNotLoadedException(Throwable cause) {
        super(cause);
    }

    public StaticContentNotLoadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.sdl.webapp.common;

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

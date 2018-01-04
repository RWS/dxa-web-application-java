package com.sdl.webapp.common.exceptions;

/**
 * @dxa.publicApi
 */
public class DxaException extends Exception {

    public DxaException() {
    }

    public DxaException(String message, Throwable innerException) {
        super(message, innerException);

    }

    public DxaException(String message) {
        super(message);
    }

    public DxaException(Throwable cause) {
        super(cause);
    }

    public DxaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

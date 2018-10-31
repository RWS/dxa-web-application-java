package com.sdl.webapp.common.exceptions;

/**
 * @dxa.publicApi
 */
public class DxaRuntimeException extends RuntimeException {
    public DxaRuntimeException() {
        super();
    }

    public DxaRuntimeException(String message) {
        super(message);
    }

    public DxaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DxaRuntimeException(Throwable cause) {
        super(cause);
    }
}

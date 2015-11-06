package com.sdl.webapp.common.exceptions;

public class DxaException extends Exception {
    public DxaException(String message, Exception innerException) {
        super(message, innerException);

    }

    public DxaException(String message) {
        super(message);
    }

    public DxaException(Throwable cause) {
        super(cause);
    }
}

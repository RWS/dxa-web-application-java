package com.sdl.dxa.exception;

public class DxaTridionCommonException extends RuntimeException {
    public DxaTridionCommonException() {
        super();
    }

    public DxaTridionCommonException(String message) {
        super(message);
    }

    public DxaTridionCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public DxaTridionCommonException(Throwable cause) {
        super(cause);
    }

    protected DxaTridionCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

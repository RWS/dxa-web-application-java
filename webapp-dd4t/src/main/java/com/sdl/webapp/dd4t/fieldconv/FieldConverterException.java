package com.sdl.webapp.dd4t.fieldconv;

public class FieldConverterException extends Exception {

    public FieldConverterException() {
    }

    public FieldConverterException(String message) {
        super(message);
    }

    public FieldConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldConverterException(Throwable cause) {
        super(cause);
    }

    public FieldConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.sdl.webapp.dd4t.fieldconv;

public class UnsupportedTargetTypeException extends FieldConverterException {

    public UnsupportedTargetTypeException() {
    }

    public UnsupportedTargetTypeException(String message) {
        super(message);
    }

    public UnsupportedTargetTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedTargetTypeException(Throwable cause) {
        super(cause);
    }

    public UnsupportedTargetTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

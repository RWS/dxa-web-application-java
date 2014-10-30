package com.sdl.webapp.dd4t.fieldconverters;

public class UnsupportedFieldTypeException extends FieldConverterException {

    public UnsupportedFieldTypeException() {
    }

    public UnsupportedFieldTypeException(String message) {
        super(message);
    }

    public UnsupportedFieldTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedFieldTypeException(Throwable cause) {
        super(cause);
    }

    public UnsupportedFieldTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

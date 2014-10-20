package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.ContentProviderException;

public class UnsupportedEntityTypeException extends ContentProviderException {

    public UnsupportedEntityTypeException() {
    }

    public UnsupportedEntityTypeException(String message) {
        super(message);
    }

    public UnsupportedEntityTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedEntityTypeException(Throwable cause) {
        super(cause);
    }

    public UnsupportedEntityTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.sdl.webapp.common.api.localization;

public class LocalizationFactoryException extends Exception {

    public LocalizationFactoryException() {
    }

    public LocalizationFactoryException(String message) {
        super(message);
    }

    public LocalizationFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalizationFactoryException(Throwable cause) {
        super(cause);
    }

    public LocalizationFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

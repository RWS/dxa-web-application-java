package com.sdl.dxa.tridion.pcaclient;

public class PCAConfigurationException extends RuntimeException {
    public PCAConfigurationException() {
    }

    public PCAConfigurationException(String message) {
        super(message);
    }

    public PCAConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PCAConfigurationException(Throwable cause) {
        super(cause);
    }

    public PCAConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

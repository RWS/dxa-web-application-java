package com.sdl.dxa.tridion.pcaclient;

public class ApiClientConfigurationException extends RuntimeException {
    public ApiClientConfigurationException() {
    }

    public ApiClientConfigurationException(String message) {
        super(message);
    }

    public ApiClientConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiClientConfigurationException(Throwable cause) {
        super(cause);
    }

    public ApiClientConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

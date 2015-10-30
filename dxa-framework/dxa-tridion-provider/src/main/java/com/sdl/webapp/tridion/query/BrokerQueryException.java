package com.sdl.webapp.tridion.query;

public class BrokerQueryException extends Exception {

    public BrokerQueryException() {
    }

    public BrokerQueryException(String message) {
        super(message);
    }

    public BrokerQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public BrokerQueryException(Throwable cause) {
        super(cause);
    }

    public BrokerQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.sdl.webapp.tridion.query;

/**
 * <p>BrokerQueryException class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class BrokerQueryException extends Exception {

    /**
     * <p>Constructor for BrokerQueryException.</p>
     */
    public BrokerQueryException() {
    }

    /**
     * <p>Constructor for BrokerQueryException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public BrokerQueryException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for BrokerQueryException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public BrokerQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for BrokerQueryException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public BrokerQueryException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for BrokerQueryException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public BrokerQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

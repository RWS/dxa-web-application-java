package org.dd4t.core.exceptions;

public class SerializationException extends Exception {

    private static final long serialVersionUID = 1785726125343L;

    public SerializationException() {
        super();
    }

    public SerializationException(final String message) {
        super(message);
    }

    public SerializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SerializationException(final Throwable cause) {
        super(cause);
    }

    protected SerializationException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package org.dd4t.core.exceptions;

public class SerializationException extends FactoryException {

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
}

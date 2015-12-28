package org.dd4t.contentmodel.exceptions;

public class NotAuthenticatedException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -1393489613071343863L;

    public NotAuthenticatedException (String message) {
        super(message);
    }

    public NotAuthenticatedException (Throwable t) {
        super(t);
    }

    public NotAuthenticatedException (String message, Throwable t) {
        super(message, t);
    }
}

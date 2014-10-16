package org.dd4t.contentmodel.exceptions;

public class NotAuthorizedException extends Exception {

    private static final long serialVersionUID = 7860070186906683875L;

    public NotAuthorizedException(String message) {
        super(message);
    }

    public NotAuthorizedException(Throwable t) {
        super(t);
    }

    public NotAuthorizedException(String message, Throwable t) {
        super(message, t);
    }
}

package org.dd4t.core.exceptions;

public class ItemNotFoundException extends Exception {

    private static final long serialVersionUID = 8243724759254216595L;

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Throwable t) {
        super(t);
    }

    public ItemNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}

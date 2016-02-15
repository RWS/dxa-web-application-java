package com.sdl.webapp.common.exceptions;

/**
 * <p>DxaException class.</p>
 */
public class DxaException extends Exception {
    /**
     * <p>Constructor for DxaException.</p>
     *
     * @param message        a {@link java.lang.String} object.
     * @param innerException a {@link java.lang.Exception} object.
     */
    public DxaException(String message, Exception innerException) {
        super(message, innerException);

    }

    /**
     * <p>Constructor for DxaException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public DxaException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for DxaException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public DxaException(Throwable cause) {
        super(cause);
    }
}

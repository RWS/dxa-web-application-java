package com.sdl.webapp.common.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>NotFoundException class.</p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public final class NotFoundException extends RuntimeException {

    /**
     * <p>Constructor for NotFoundException.</p>
     */
    public NotFoundException() {
    }

    /**
     * <p>Constructor for NotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for NotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for NotFoundException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public NotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for NotFoundException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

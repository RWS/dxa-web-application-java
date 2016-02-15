package com.sdl.webapp.common.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>InternalServerErrorException class.</p>
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
    /**
     * <p>Constructor for InternalServerErrorException.</p>
     */
    public InternalServerErrorException() {
    }

    /**
     * <p>Constructor for InternalServerErrorException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public InternalServerErrorException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for InternalServerErrorException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for InternalServerErrorException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for InternalServerErrorException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public InternalServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

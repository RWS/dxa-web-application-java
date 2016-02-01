package com.sdl.webapp.common.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>BadRequestException class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * <p>Constructor for BadRequestException.</p>
     */
    public BadRequestException() {
    }

    /**
     * <p>Constructor for BadRequestException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for BadRequestException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for BadRequestException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public BadRequestException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for BadRequestException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

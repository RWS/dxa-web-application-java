package com.sdl.webapp.common.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>ForbiddenException class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    /**
     * <p>Constructor for ForbiddenException.</p>
     */
    public ForbiddenException() {
    }

    /**
     * <p>Constructor for ForbiddenException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public ForbiddenException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for ForbiddenException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for ForbiddenException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for ForbiddenException.</p>
     *
     * @param message            a {@link java.lang.String} object.
     * @param cause              a {@link java.lang.Throwable} object.
     * @param enableSuppression  a boolean.
     * @param writableStackTrace a boolean.
     */
    public ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

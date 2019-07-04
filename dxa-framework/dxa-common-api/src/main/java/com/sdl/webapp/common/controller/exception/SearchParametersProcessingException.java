package com.sdl.webapp.common.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Used for wrapping exceptions related to parsing search parameters.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SearchParametersProcessingException extends IllegalArgumentException {

    /**
     * Creates new instance of SearchParametersProcessingException.
     * @param message Message
     */
    public SearchParametersProcessingException(String message) {
        super(message);
    }

    /**
     * Creates new instance of SearchParametersProcessingException.
     * @param message Message
     * @param cause Original exception
     */
    public SearchParametersProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates new instance of SearchParametersProcessingException.
     * @param cause Original exception
     */
    public SearchParametersProcessingException(Throwable cause) {
        super(cause);
    }
}

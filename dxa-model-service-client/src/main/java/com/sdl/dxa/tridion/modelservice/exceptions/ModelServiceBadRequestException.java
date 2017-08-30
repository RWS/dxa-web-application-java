package com.sdl.dxa.tridion.modelservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Bad request to model service.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ModelServiceBadRequestException extends RuntimeException {

    public ModelServiceBadRequestException() {
    }

    public ModelServiceBadRequestException(String message) {
        super(message);
    }

    public ModelServiceBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelServiceBadRequestException(Throwable cause) {
        super(cause);
    }

    public ModelServiceBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

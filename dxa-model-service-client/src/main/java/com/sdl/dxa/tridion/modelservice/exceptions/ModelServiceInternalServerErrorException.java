package com.sdl.dxa.tridion.modelservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * If an error happened in the Model Service.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ModelServiceInternalServerErrorException extends RuntimeException {

    public ModelServiceInternalServerErrorException() {
    }

    public ModelServiceInternalServerErrorException(String message) {
        super(message);
    }

    public ModelServiceInternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelServiceInternalServerErrorException(Throwable cause) {
        super(cause);
    }

    public ModelServiceInternalServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ModelServiceInternalServerErrorException(String message, HttpStatusCodeException e) {
    }
}

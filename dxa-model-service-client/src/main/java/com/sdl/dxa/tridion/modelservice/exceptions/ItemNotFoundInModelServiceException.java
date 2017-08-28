package com.sdl.dxa.tridion.modelservice.exceptions;

/**
 * If item is not found with Model Service.
 */
public class ItemNotFoundInModelServiceException extends Exception {

    public ItemNotFoundInModelServiceException() {
    }

    public ItemNotFoundInModelServiceException(String message) {
        super(message);
    }

    public ItemNotFoundInModelServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotFoundInModelServiceException(Throwable cause) {
        super(cause);
    }

    public ItemNotFoundInModelServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

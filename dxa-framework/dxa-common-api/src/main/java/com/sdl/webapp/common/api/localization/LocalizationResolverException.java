package com.sdl.webapp.common.api.localization;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an error occurs in a localization resolver when resolving a localization.
 * @dxa.publicApi
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class LocalizationResolverException extends Exception {

    public LocalizationResolverException() {
    }

    public LocalizationResolverException(String message) {
        super(message);
    }

    public LocalizationResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalizationResolverException(Throwable cause) {
        super(cause);
    }

    public LocalizationResolverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

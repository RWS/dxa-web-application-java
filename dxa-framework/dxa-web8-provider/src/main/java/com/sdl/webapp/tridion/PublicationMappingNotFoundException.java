package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.localization.LocalizationResolverException;

public class PublicationMappingNotFoundException extends LocalizationResolverException {

    public PublicationMappingNotFoundException() {
    }

    public PublicationMappingNotFoundException(String message) {
        super(message);
    }

    public PublicationMappingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PublicationMappingNotFoundException(Throwable cause) {
        super(cause);
    }

    public PublicationMappingNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

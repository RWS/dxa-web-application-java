package com.sdl.webapp.common.api.localization;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Localization not found!")
public class LocalizationNotFoundException extends LocalizationNotResolvedException {

    public LocalizationNotFoundException(String message) {
        super(message);
    }

    public LocalizationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

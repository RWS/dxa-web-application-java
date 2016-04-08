package com.sdl.webapp.common.api.localization;

import com.sdl.webapp.common.controller.exception.NotFoundException;

public class LocalizationNotFoundException extends NotFoundException {

    public LocalizationNotFoundException(String message) {
        super(message);
    }

    public LocalizationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

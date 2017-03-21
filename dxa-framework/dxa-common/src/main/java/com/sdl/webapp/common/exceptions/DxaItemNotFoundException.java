package com.sdl.webapp.common.exceptions;

import com.sdl.webapp.common.api.content.ContentProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Item not found", code = HttpStatus.NOT_FOUND)
public class DxaItemNotFoundException extends ContentProviderException {

    public DxaItemNotFoundException() {
    }

    public DxaItemNotFoundException(String message) {
        super(message);
    }

    public DxaItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DxaItemNotFoundException(Throwable cause) {
        super(cause);
    }

    public DxaItemNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


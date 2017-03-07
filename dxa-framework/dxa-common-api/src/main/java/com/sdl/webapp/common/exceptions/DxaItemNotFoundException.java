package com.sdl.webapp.common.exceptions;

import com.sdl.webapp.common.api.content.ContentProviderException;

public class DxaItemNotFoundException extends ContentProviderException {

    public DxaItemNotFoundException(String message, Exception innerException) {
        super(message, innerException);

    }

    public DxaItemNotFoundException(String message) {
        super(message);
    }
}


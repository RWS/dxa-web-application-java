package com.sdl.webapp.common.exceptions;

/**
 * Created by Administrator on 15/09/2015.
 */
public class DxaItemNotFoundException extends DxaException {

    public DxaItemNotFoundException(String message, Exception innerException) {
        super(message, innerException);

    }

    public DxaItemNotFoundException(String message) {
        super(message);
    }
}


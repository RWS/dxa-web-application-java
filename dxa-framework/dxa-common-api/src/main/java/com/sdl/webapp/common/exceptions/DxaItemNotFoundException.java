package com.sdl.webapp.common.exceptions;

/**
 * Created by Administrator on 15/09/2015.
 */
public class DxaItemNotFoundException extends DxaException {

    /**
     * <p>Constructor for DxaItemNotFoundException.</p>
     *
     * @param message        a {@link java.lang.String} object.
     * @param innerException a {@link java.lang.Exception} object.
     */
    public DxaItemNotFoundException(String message, Exception innerException) {
        super(message, innerException);

    }

    /**
     * <p>Constructor for DxaItemNotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public DxaItemNotFoundException(String message) {
        super(message);
    }
}


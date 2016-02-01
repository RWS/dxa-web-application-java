package com.sdl.webapp.common.api.model.entity;

/**
 * Created by Administrator on 2/10/2015.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class ExceptionEntity extends AbstractEntityModel {
    private Exception exception;

    /**
     * <p>Constructor for ExceptionEntity.</p>
     *
     * @param ex a {@link java.lang.Exception} object.
     */
    public ExceptionEntity(Exception ex) {
        this.exception = ex;
    }

    /**
     * <p>Getter for the field <code>exception</code>.</p>
     *
     * @return a {@link java.lang.Exception} object.
     */
    public Exception getException() {
        return this.exception;
    }
}

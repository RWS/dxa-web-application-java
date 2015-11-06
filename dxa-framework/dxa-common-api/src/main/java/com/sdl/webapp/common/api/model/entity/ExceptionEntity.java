package com.sdl.webapp.common.api.model.entity;

/**
 * Created by Administrator on 2/10/2015.
 */
public class ExceptionEntity extends AbstractEntityModel {
    private Exception exception;

    public ExceptionEntity(Exception ex) {
        this.exception = ex;
    }

    public Exception getException() {
        return this.exception;
    }
}

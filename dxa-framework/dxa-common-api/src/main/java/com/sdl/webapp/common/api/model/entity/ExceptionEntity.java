package com.sdl.webapp.common.api.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExceptionEntity extends AbstractEntityModel {
    private Exception exception;

    public ExceptionEntity(Exception ex) {
        this.exception = ex;
    }
}

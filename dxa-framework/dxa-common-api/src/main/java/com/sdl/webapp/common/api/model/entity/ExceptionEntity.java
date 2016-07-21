package com.sdl.webapp.common.api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ExceptionEntity extends AbstractEntityModel {

    private Exception exception;
}

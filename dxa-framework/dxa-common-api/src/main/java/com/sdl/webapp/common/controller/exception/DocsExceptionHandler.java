package com.sdl.webapp.common.controller.exception;

import com.sdl.webapp.common.impl.model.ErrorMessage;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * DocsExceptionHandler class. It handles exceptions which annotated with @ResponseStatus anno,
 * and returns its status code, otherwise it returns BAD_REQUEST or INTERNAL_SERVER_ERROR
 */
@Component
public class DocsExceptionHandler {

    public ErrorMessage handleException(Exception ex) {
        ResponseStatus annotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        String message = ex.getMessage();
        if (message == null) {
            // Put the type on the message
            message = ex.getClass().getName();
        }

        if (annotation != null) {
            return new ErrorMessage(message, annotation.value());
        }
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        // Happens when input params are of an invalid data type
        if (ex instanceof TypeMismatchException) {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ErrorMessage(message, status);
    }
}

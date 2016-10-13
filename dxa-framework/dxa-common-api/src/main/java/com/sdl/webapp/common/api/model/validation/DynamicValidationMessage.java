package com.sdl.webapp.common.api.model.validation;

import com.sdl.webapp.common.api.model.ViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a provider of custom validation message with a code.
 * That means that error code is to be resolved by {@link DynamicCodeResolver#resolveCode(String, ViewModel)}.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicValidationMessage {

    String errorCode();
}

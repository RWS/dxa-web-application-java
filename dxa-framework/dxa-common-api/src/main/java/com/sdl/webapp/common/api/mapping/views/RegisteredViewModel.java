package com.sdl.webapp.common.api.mapping.views;

import com.sdl.webapp.common.api.model.ViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that wires view name and a model class representing data for this view with optional controller name.
 * @dxa.publicApi
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisteredViewModel {

    String viewName() default "";

    Class<? extends ViewModel> modelClass();

    String controllerName() default "";
}

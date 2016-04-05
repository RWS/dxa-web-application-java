package com.sdl.webapp.common.api.mapping.views;

import com.sdl.webapp.common.api.model.ViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which wires view name and a class representing data for this view.
 * @deprecated since DXA 1.4, use {@link RegisteredViewModel} instead
 */
@SuppressWarnings("WeakerAccess")
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
// todo dxa2 remove
public @interface RegisteredView {
    String viewName();

    Class<? extends ViewModel> clazz();

    String controllerName() default "";
}

package com.sdl.webapp.common.api.mapping.views;

import com.sdl.webapp.common.api.model.ViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that wires view name and a model class representing data for this view with optional controller name.
 *
 * @since 1.4 as a replacement for {@link RegisteredView}
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisteredModelView {
    String viewName();

    Class<? extends ViewModel> modelClass();

    String controllerName() default "";
}

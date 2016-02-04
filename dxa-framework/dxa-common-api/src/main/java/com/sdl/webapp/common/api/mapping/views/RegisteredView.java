package com.sdl.webapp.common.api.mapping.views;

import com.sdl.webapp.common.api.model.ViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>RegisteredView class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisteredView {
    String viewName();

    Class<? extends ViewModel> clazz();

    String controllerName() default "";
}

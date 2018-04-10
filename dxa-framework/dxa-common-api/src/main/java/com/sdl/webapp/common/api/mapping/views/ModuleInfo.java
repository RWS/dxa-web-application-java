package com.sdl.webapp.common.api.mapping.views;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Module formal description.
 * @dxa.publicApi
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    /**
     * Name of a module.
     * @return name of a module
     */
    String name();

    /**
     * Name of the Mvc area.
     * @return name of the Mvc area
     */
    String areaName();

    /**
     * Short description of a module.
     * @return a short description of a module
     */
    String description() default "";

    /**
     * If this module initialization should be skipped.
     * @return whether this module shouldn't be initialized
     */
    boolean skip() default false;
}

package com.sdl.webapp.common.api.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SemanticEntity {

    String entityName() default "";

    String value() default "";

    String vocabulary() default "";

    String prefix() default "";

    boolean public_() default false;
}

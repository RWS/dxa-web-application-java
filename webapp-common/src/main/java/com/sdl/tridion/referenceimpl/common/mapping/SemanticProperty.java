package com.sdl.tridion.referenceimpl.common.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SemanticProperty {

    String value() default "";

    boolean ignoreMapping() default false;
}

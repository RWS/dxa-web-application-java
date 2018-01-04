package com.sdl.dxa.caching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates types that should never be cached.
 *
 * @dxa.publicApi
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NeverCached {

    /**
     * Qualifier of the annotated model to be used as a one-to-one identifier of the type.
     *
     * @return alias aka qualifier of the annotated model
     */
    String qualifier();
}

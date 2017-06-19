package com.sdl.dxa.caching;

import com.sdl.webapp.common.api.model.ViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates types that should never be cached to be used with subtypes of {@link ViewModel}.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeverCached {

    /**
     * Qualifier of the annotated model to be used as a one-to-one identifier of the type.
     *
     * @return alias aka qualifier of the annotated model
     */
    String qualifier();
}

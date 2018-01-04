package com.sdl.webapp.common.api.serialization.json.annotation;

import com.sdl.webapp.common.api.serialization.json.filter.XpmAwareJsonFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells {@link XpmAwareJsonFilter} to hide the specified element if XPM is not enabled.
 * @dxa.publicApi
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonXpmAware {

}

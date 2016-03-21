package com.sdl.webapp.common.api.mapping.views;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set of views to be registered by {@link com.sdl.webapp.common.api.mapping.views.AbstractInitializer}.
 * @deprecated since DXA 1.4, use {@link RegisteredViewModels} instead
 */
@SuppressWarnings("WeakerAccess")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
// todo dxa2 remove
public @interface RegisteredViews {
    RegisteredView[] value();
}

package com.sdl.webapp.common.api.mapping.views;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set of viewModels to be registered by {@link com.sdl.webapp.common.api.mapping.views.AbstractInitializer}.
 *
 * @since 1.4 as a replacement for {@link RegisteredViews}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisteredModelViews {
    RegisteredModelView[] value();
}

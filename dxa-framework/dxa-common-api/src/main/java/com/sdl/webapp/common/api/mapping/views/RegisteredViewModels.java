package com.sdl.webapp.common.api.mapping.views;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set of viewModels to be registered by {@link AbstractModuleInitializer}.
 *
 * @dxa.publicApi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisteredViewModels {

    RegisteredViewModel[] value();
}

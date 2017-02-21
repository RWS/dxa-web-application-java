package com.sdl.dxa;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation/shortcut for R2 pipeline profile. This annotation should be put on {@link Component}s and subclasses
 * to conditionally enable them in case of presence of {@code r2.provider} Spring profile.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Profile("r2.provider")
@Primary
public @interface R2 {

}

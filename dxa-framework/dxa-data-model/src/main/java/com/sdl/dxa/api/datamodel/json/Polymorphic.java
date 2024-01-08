package com.sdl.dxa.api.datamodel.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation that marks object as polymorphic meaning that it may have subclasses.
 * Used by {@link ObjectMapper} to set {@link PolymorphicObjectMixin} in {@link DataModelSpringConfiguration}.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Polymorphic {

}

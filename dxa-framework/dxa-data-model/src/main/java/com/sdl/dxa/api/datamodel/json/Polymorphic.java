package com.sdl.dxa.api.datamodel.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;

/**
 * Annotation that marks object as polymorphic.
 * Used by {@link ObjectMapper} to set {@link PolymorphicObjectMixin} in {@link DataModelSpringConfiguration}.
 */
public @interface Polymorphic {

}

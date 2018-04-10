package com.sdl.dxa.api.datamodel.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;

/**
 * Annotation that marks object as polymorphic meaning that it may have subclasses.
 * Used by {@link ObjectMapper} to set {@link PolymorphicObjectMixin} in {@link DataModelSpringConfiguration}.
 *
 * @dxa.publicApi
 */
public @interface Polymorphic {

}

package com.sdl.webapp.common.api.mapping;

import com.sdl.webapp.common.api.mapping.config.SemanticSchemaField;

/**
 * TODO: Documentation.
 */
public interface SemanticFieldDataProvider {

    // TODO: Design this interface.
    // Should provide a method to get the data for a field from a SemanticSchemaField and FieldSemantics

    Object getFieldData(SemanticSchemaField schemaField);
}

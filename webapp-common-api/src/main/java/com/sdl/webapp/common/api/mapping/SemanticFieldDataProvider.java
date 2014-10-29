package com.sdl.webapp.common.api.mapping;

import com.sdl.webapp.common.api.mapping.config.SemanticField;
import org.springframework.core.convert.TypeDescriptor;

/**
 * TODO: Documentation.
 */
public interface SemanticFieldDataProvider {

    Object getFieldData(SemanticField semanticField, TypeDescriptor dataTypeDescriptor);
}

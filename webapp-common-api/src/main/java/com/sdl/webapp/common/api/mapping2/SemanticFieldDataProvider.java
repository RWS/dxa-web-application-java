package com.sdl.webapp.common.api.mapping2;

import com.sdl.webapp.common.api.mapping2.config.SemanticField;
import org.springframework.core.convert.TypeDescriptor;

public interface SemanticFieldDataProvider {

    Object getFieldData(SemanticField semanticField, TypeDescriptor dataTypeDescriptor);
}

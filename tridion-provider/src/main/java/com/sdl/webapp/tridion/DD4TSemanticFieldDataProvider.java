package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.tridion.fieldconverters.*;

import org.dd4t.contentmodel.*;

/**
 * Implementation of {@code SemanticFieldDataProvider} that gets field data from the DD4T model.
 */
public class DD4TSemanticFieldDataProvider extends AbstractSemanticFieldDataProvider implements SemanticFieldDataProvider {

    public DD4TSemanticFieldDataProvider(Component component, FieldConverterRegistry fieldConverterRegistry, ModelBuilderPipeline builder) {
        super(component, fieldConverterRegistry, builder);
    }
}

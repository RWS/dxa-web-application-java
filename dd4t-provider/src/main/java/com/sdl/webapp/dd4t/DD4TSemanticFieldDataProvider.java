package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.FieldData;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.FieldPath;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.dd4t.fieldconverters.*;

import org.dd4t.contentmodel.*;
import org.dd4t.contentmodel.impl.BaseField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Implementation of {@code SemanticFieldDataProvider} that gets field data from the DD4T model.
 */
public class DD4TSemanticFieldDataProvider extends AbstractSemanticFieldDataProvider implements SemanticFieldDataProvider {

    public DD4TSemanticFieldDataProvider(Component component, FieldConverterRegistry fieldConverterRegistry, EntityBuilder builder) {
        super(component, fieldConverterRegistry, builder);
    }
}

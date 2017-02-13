package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.webapp.common.api.mapping.semantic.FieldData;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Map;

@Slf4j
public class DefaultSemanticFieldDataProvider implements SemanticFieldDataProvider {

    private ViewModelData model;

    private DefaultSemanticFieldDataProvider() {
    }

    public static SemanticFieldDataProvider getFor(ViewModelData model) {
        DefaultSemanticFieldDataProvider dataProvider = new DefaultSemanticFieldDataProvider();
        dataProvider.model = model;
        return dataProvider;
    }

    @Override
    public FieldData getFieldData(SemanticField semanticField, TypeDescriptor targetType) throws SemanticMappingException {
        log.trace("semanticField: {}, targetType: {}", semanticField, targetType);

        return null;
    }

    @Override
    public Object getSelfFieldData(TypeDescriptor targetType) throws SemanticMappingException {
        return null;
    }

    @Override
    public Map<String, String> getAllFieldData() throws SemanticMappingException {
        return null;
    }


}

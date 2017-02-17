package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.ExceptionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ContentModelDataConverter implements SourceConverter<ContentModelData> {

    private final SemanticMapper semanticMapper;

    @Autowired
    public ContentModelDataConverter(SemanticMapper semanticMapper) {
        this.semanticMapper = semanticMapper;
    }

    @Override
    public List<Class<? extends ContentModelData>> getTypes() {
        return Collections.singletonList(ContentModelData.class);
    }

    @Override
    public Object convert(ContentModelData toConvert, TypeDescriptor targetType, SemanticField semanticField,
                          DefaultSemanticFieldDataProvider dataProvider) {
        Class<?> objectType = targetType.getObjectType();
        try {
            return semanticMapper.createEntity(objectType.asSubclass(EntityModel.class),
                    semanticField.getEmbeddedFields(), dataProvider.embedded(toConvert));
        } catch (SemanticMappingException e) {
            log.warn("Cannot perform conversion for embedded entity, objectType {}, semantic field {}, value to convert {}",
                    objectType, semanticField, toConvert, e);
            return new ExceptionEntity(e);
        }
    }
}

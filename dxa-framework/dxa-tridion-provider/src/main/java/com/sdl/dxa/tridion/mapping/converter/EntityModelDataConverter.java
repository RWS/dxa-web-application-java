package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class EntityModelDataConverter implements SemanticModelConverter<EntityModelData> {

    @Override
    public Object convert(EntityModelData toConvert, TypeInformation targetType, SemanticField semanticField,
                          ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        Class<?> objectType = targetType.getObjectType();
        Object result;

        if (String.class.isAssignableFrom(objectType)) {
            result = toConvert.getLinkUrl();
        } else if (Link.class.isAssignableFrom(objectType)) {
            Link link = new Link();
            link.setId(toConvert.getId());
            link.setUrl(toConvert.getLinkUrl());
            result = link;
        } else {
            try {
                if (EntityModel.class.isAssignableFrom(objectType)) {
                    result = pipeline.createEntityModel(toConvert, objectType.asSubclass(EntityModel.class));
                } else {
                    throw new FieldConverterException("Object type " + objectType + " is not supported by EntityModelDataConverter");
                }
            } catch (DxaException e) {
                throw new FieldConverterException("Cannot convert a entity model " + toConvert.getId() +
                        " to " + objectType + " for semantic field " + semanticField.getName(), e);
            }
        }

        return convertToCollectionIfNeeded(result, targetType);
    }

    @Override
    public List<Class<? extends EntityModelData>> getTypes() {
        return Collections.singletonList(EntityModelData.class);
    }
}

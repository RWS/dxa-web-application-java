package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ContentModelDataConverter implements SemanticModelConverter<ContentModelData> {

    private final SemanticMapper semanticMapper;

    @Autowired
    public ContentModelDataConverter(SemanticMapper semanticMapper) {
        this.semanticMapper = semanticMapper;
    }

    @Override
    public Object convert(ContentModelData toConvert, TypeInformation targetType, SemanticField semanticField,
                          ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        Class<?> objectType = targetType.getObjectType();

        try {
            ViewModel entity = semanticMapper.createEntity(objectType.asSubclass(EntityModel.class),
                    semanticField.getEmbeddedFields(), dataProvider.embedded(toConvert));

            return convertToCollectionIfNeeded(entity, targetType);
        } catch (SemanticMappingException e) {
            throw new FieldConverterException("Cannot perform conversion for embedded entity, objectType " + objectType + ", " +
                    "semantic field " + semanticField + ", value to convert " + toConvert, e);
        }
    }

    @Override
    public List<Class<? extends ContentModelData>> getTypes() {
        return Collections.singletonList(ContentModelData.class);
    }

}

package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.util.ListWrapper;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
public class ListWrapperConverter implements SemanticModelConverter<ListWrapper> {

    // can't be in a constructor because of circular dependency
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private GenericSemanticModelDataConverter genericSemanticModelDataConverter;

    @Override
    public Object convert(ListWrapper toConvert, TypeInformation targetType, SemanticField semanticField, ModelBuilderPipeline pipeline,
                          DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        TypeDescriptor elementType = TypeDescriptor.valueOf(targetType.getObjectType());

        if (targetType.isCollection()) {
            Collection<Object> result = targetType.getCollectionType() == List.class ? new ArrayList<>() : new HashSet<>();

            List<?> values = ((ListWrapper<?>) toConvert).getValues();
            for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
                Object value = values.get(i);
                result.add(convertValue(value, elementType, semanticField, pipeline, dataProvider, i));
            }

            return result;
        } else {
            return convertValue(toConvert.getValues().get(0), elementType, semanticField, pipeline, dataProvider, 0);
        }
    }

    @Override
    public List<Class<? extends ListWrapper>> getTypes() {
        return Arrays.asList(ListWrapper.class,
                ListWrapper.ContentModelDataListWrapper.class,
                ListWrapper.KeywordModelDataListWrapper.class,
                ListWrapper.EntityModelDataListWrapper.class,
                ListWrapper.RichTextDataListWrapper.class);
    }

    @NotNull
    private Object convertValue(Object toConvert, TypeDescriptor targetType, SemanticField semanticField,
                                ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider, int index) throws FieldConverterException {
        return genericSemanticModelDataConverter.convert(toConvert, targetType, semanticField, pipeline, dataProvider.iteration(toConvert, semanticField, index));
    }
}

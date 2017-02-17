package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.util.ListWrapper;
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
public class ListWrapperConverter implements SourceConverter<ListWrapper> {

    // can't be in a constructor because of circular dependency
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private SourceConverterFactory sourceConverterFactory;

    @Override
    public List<Class<? extends ListWrapper>> getTypes() {
        return Arrays.asList(ListWrapper.class,
                ListWrapper.ContentModelDataListWrapper.class,
                ListWrapper.KeywordModelDataListWrapper.class,
                ListWrapper.EntityModelDataListWrapper.class,
                ListWrapper.RichTextDataListWrapper.class);
    }

    @Override
    public Object convert(ListWrapper toConvert, TypeDescriptor targetType, SemanticField semanticField, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        if (targetType.isCollection()) {
            Collection<Object> result = targetType.getObjectType() == List.class ? new ArrayList<>() : new HashSet<>();

            for (Object value : ((ListWrapper<?>) toConvert).getValues()) {
                result.add(convertValue(value, targetType.getElementTypeDescriptor(), semanticField, dataProvider));
            }

            return result;
        } else {
            return convertValue(toConvert.getValues().get(0), targetType, semanticField, dataProvider);
        }
    }

    @NotNull
    private Object convertValue(Object toConvert, TypeDescriptor targetType, SemanticField semanticField, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        return sourceConverterFactory.convert(toConvert, targetType, semanticField, dataProvider.embedded(toConvert));
    }
}

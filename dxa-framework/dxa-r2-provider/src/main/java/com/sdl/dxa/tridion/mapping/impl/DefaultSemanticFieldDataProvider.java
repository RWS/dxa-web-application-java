package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.dxa.tridion.mapping.converter.SourceConverterFactory;
import com.sdl.webapp.common.api.mapping.semantic.FieldData;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldPath;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Map;

@Slf4j
public class DefaultSemanticFieldDataProvider implements SemanticFieldDataProvider {

    private ViewModelDataWrapper dataWrapper;

    private SourceConverterFactory sourceConverterFactory;

    private int embeddingLevel = 0;

    private DefaultSemanticFieldDataProvider(ViewModelDataWrapper dataWrapper) {
        this.dataWrapper = dataWrapper;
        this.sourceConverterFactory = ApplicationContextHolder.getContext().getBean(SourceConverterFactory.class);
    }

    public static DefaultSemanticFieldDataProvider getFor(ViewModelData model) {
        return new DefaultSemanticFieldDataProvider(new ViewModelDataWrapper() {
            @Override
            public ContentModelData getContent() {
                return model instanceof EntityModelData ? ((EntityModelData) model).getContent() : ContentModelData.EMPTY;
            }

            @Override
            public ContentModelData getMetadata() {
                return model.getMetadata();
            }

            @Override
            public Object getWrappedModel() {
                return model;
            }
        });
    }

    public DefaultSemanticFieldDataProvider embedded(Object value) {
        if (!(value instanceof ContentModelData)) {
            log.debug("Type {} is not supported by embedded SemanticFieldDataProvider", value.getClass());
            return null;
        }
        ContentModelData modelData = (ContentModelData) value;
        DefaultSemanticFieldDataProvider provider = new DefaultSemanticFieldDataProvider(new ViewModelDataWrapper() {
            @Override
            public ContentModelData getContent() {
                return modelData;
            }

            @Override
            public ContentModelData getMetadata() {
                return ContentModelData.EMPTY;
            }

            @Override
            public Object getWrappedModel() {
                return value;
            }
        });
        provider.embeddingLevel++;
        return provider;
    }

    @Override
    public FieldData getFieldData(SemanticField semanticField, TypeDescriptor targetType) throws SemanticMappingException {
        log.trace("semanticField: {}, targetType: {}", semanticField, targetType);

        ContentModelData data = semanticField.getPath().isMetadata() ? dataWrapper.getMetadata() : dataWrapper.getContent();
        FieldPath path = getCurrentPath(semanticField);

        if (path == null) {
            log.warn("Path is null for semantic field {}, embedding level {}", semanticField, embeddingLevel);
            return null;
        }

        if (data.isEmpty() || !data.containsKey(path.getHead())) {
            log.debug("No data is found for path {} for semantic field", path, semanticField);
            return null;
        }

        Object toConvert = data.get(path.getHead());
        Object value = sourceConverterFactory.convert(toConvert, targetType, semanticField, this);

        return new FieldData(value, semanticField.getXPath(null));
    }

    @Nullable
    private FieldPath getCurrentPath(SemanticField semanticField) {
        FieldPath _path = semanticField.getPath().getTail();
        for (int i = 0; i < embeddingLevel && _path != null; i++) {
            _path = _path.getTail();
        }
        return _path;
    }

    @Override
    public Object getSelfFieldData(TypeDescriptor targetType) throws SemanticMappingException {
        Class<?> objectType = targetType.getObjectType();
        if (MediaItem.class.isAssignableFrom(objectType) || Link.class.isAssignableFrom(objectType) || String.class.isAssignableFrom(objectType)) {
            return sourceConverterFactory.selfLink(dataWrapper.getWrappedModel(), targetType);
        }
        throw new UnsupportedTargetTypeException(targetType);
    }

    @Override
    public Map<String, String> getAllFieldData() throws SemanticMappingException {
        return null;
    }

    private interface ViewModelDataWrapper {

        ContentModelData getContent();

        ContentModelData getMetadata();

        Object getWrappedModel();
    }


}

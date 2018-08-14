package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.KeywordModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.dxa.api.datamodel.model.util.CanWrapContentAndMetadata;
import com.sdl.dxa.api.datamodel.model.util.ListWrapper;
import com.sdl.dxa.api.datamodel.model.util.ModelDataWrapper;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.converter.GenericSemanticModelDataConverter;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.mapping.semantic.FieldData;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldPath;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.KeywordModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sdl.webapp.common.util.ApplicationContextHolder.getContext;

@Slf4j
public class DefaultSemanticFieldDataProvider implements SemanticFieldDataProvider {

    private ModelDataWrapper dataWrapper;

    @Getter
    private SemanticSchema semanticSchema;

    private GenericSemanticModelDataConverter genericSemanticModelDataConverter;

    private ModelBuilderPipeline pipeline;

    private int embeddingLevel = 0;

    private int iteration = 0;

    private String context = null;

    private DefaultSemanticFieldDataProvider(ModelDataWrapper dataWrapper, SemanticSchema semanticSchema) {
        this.dataWrapper = dataWrapper;
        this.semanticSchema = semanticSchema;
        this.genericSemanticModelDataConverter = getContext().getBean(GenericSemanticModelDataConverter.class);
        this.pipeline = getContext().getBean(ModelBuilderPipeline.class);
    }

    @Nullable
    public static DefaultSemanticFieldDataProvider getFor(ViewModelData model, SemanticSchema semanticSchema) {
        return _getFor(model, semanticSchema);
    }

    @Nullable
    private static DefaultSemanticFieldDataProvider _getFor(@NotNull Object model, SemanticSchema semanticSchema) {
        if (!(model instanceof CanWrapContentAndMetadata)) {
            log.debug("Type {} is not supported by embedded SemanticFieldDataProvider", model.getClass());
            return null;
        }
        return new DefaultSemanticFieldDataProvider(((CanWrapContentAndMetadata) model).getDataWrapper(), semanticSchema);
    }

    /**
     * Shifts the context of this data provider to the given index for the given mode. It is used to iterate over lists of values and to save XPath context.
     *
     * @param model         current item in the list
     * @param semanticField current parent semantic field inside which we iterate
     * @param index         current item index in a list
     * @return data provider shifted to the given index
     */
    @Nullable
    public DefaultSemanticFieldDataProvider iteration(Object model, SemanticField semanticField, int index) {
        DefaultSemanticFieldDataProvider provider = _getFor(model, semanticSchema);
        if (provider != null) {
            provider.embeddingLevel = this.embeddingLevel;
            provider.iteration = index + 1;
            provider.context = semanticField.getXPath(this.context) + "[" + provider.iteration + "]";
            log.trace("Iterating over some list, current index = {}, item = {}, embedding level = {}, context = {}",
                    index, model, this.embeddingLevel, provider.context);
        }
        return provider;
    }

    /**
     * Increases the embedding level of the data provider saving the current XPath context. It is used to go inside the nested maps.
     *
     * @param value current value in the map
     * @return data provider embedded to the next elvel
     */
    @Nullable
    public DefaultSemanticFieldDataProvider embedded(Object value) {
        DefaultSemanticFieldDataProvider provider = _getFor(value, this.semanticSchema);
        if (provider != null) {
            provider.iteration = this.iteration;
            provider.context = this.context;
            provider.embeddingLevel = this.embeddingLevel + 1;
            log.trace("Embedding into embedding level = {}, index = {}, item = {}, context = {}",
                    provider.iteration, value, this.embeddingLevel, provider.context);
        }
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

        Optional<Object> field = findField(data, path);
        if (!field.isPresent()) {
            log.debug("No data is found for path {} for semantic field", path, semanticField);
            return null;
        }

        Object value = genericSemanticModelDataConverter.convert(field.get(), targetType, semanticField, pipeline, this);

        return new FieldData(value, semanticField.getXPath(this.context));
    }

    @Override
    public Object getSelfFieldData(TypeDescriptor targetType) throws SemanticMappingException {
        Class<?> objectType = targetType.getObjectType();
        if (MediaItem.class.isAssignableFrom(objectType) || Link.class.isAssignableFrom(objectType) || String.class.isAssignableFrom(objectType)) {
            try {
                return genericSemanticModelDataConverter.selfLink(dataWrapper.getWrappedModel(), targetType, pipeline);
            } catch (DxaException e) {
                throw new SemanticMappingException("Failed self-linking " + targetType, e);
            }
        }
        throw new UnsupportedTargetTypeException(targetType);
    }

     @Override
    public <T> Map<String, T> getAllFieldData(Class<T> targetType) throws SemanticMappingException {
        
        final Map<String, T> fieldData = new HashMap<>();
         
        Stream<Map.Entry<String, Object>> content = getEntryStream(dataWrapper.getContent());
        Stream<Map.Entry<String, Object>> metadata = getEntryStream(dataWrapper.getMetadata());
                
        for (Map.Entry<String, Object> entry : Stream.concat(content, metadata).collect(Collectors.toSet())) 
          {
             this.<T>_getAllFieldsData(entry, fieldData, targetType);
          }
          
         return fieldData;
    }
    
    private Stream<Map.Entry<String, Object>> getEntryStream(ContentModelData contentModelData) {
        return contentModelData != null ? contentModelData.entrySet().stream() : Stream.empty();
    }
    
    private <T> void _getAllFieldsData(Map.Entry<String, Object> entry, Map<String, T> fieldData ,Class<T> targetType) throws FieldConverterException {
        if ("settings".equals(entry.getKey())) {
            throw new UnsupportedOperationException("'settings' field handling"); //todo dxa2 implement
        }

        Object value = entry.getValue();
        if (!fieldData.containsKey(entry.getKey()) && value != null) 
        {            
            if (targetType == KeywordModel.class ) 
            {
                if (value instanceof KeywordModelData) 
                {
                    T keyword = (T) genericSemanticModelDataConverter.convert(value, TypeDescriptor.valueOf(KeywordModel.class), null, pipeline, this);
                    
                    fieldData.put(entry.getKey(), keyword);
                }             
            }
            else if(targetType == String.class)
            {
                Optional<T> emdTcmUri = (Optional<T>) getEntityModelDataTcmUriOrNull(value);
                fieldData.put(entry.getKey(), emdTcmUri.orElse(
                    (T) genericSemanticModelDataConverter.convert(value, TypeDescriptor.valueOf(String.class), null,
                            pipeline, this)));
            }   
        }
    }

    @NotNull
    private Optional<String> getEntityModelDataTcmUriOrNull(Object possibleEmd) {
        EntityModelData modelData = null;
        if (possibleEmd instanceof EntityModelData) {
            modelData = (EntityModelData) possibleEmd;
        } else if (possibleEmd instanceof ListWrapper.EntityModelDataListWrapper) {
            modelData = ((ListWrapper.EntityModelDataListWrapper) possibleEmd).get(0);
        }

        if (modelData == null) {
            return Optional.empty();
        }

        String localizationId = getContext().getBean(WebRequestContext.class).getLocalization().getId();
        return Optional.of(TcmUtils.buildTcmUri(localizationId, modelData.getId()));
    }

    @Nullable
    private FieldPath getCurrentPath(SemanticField semanticField) {
        FieldPath _path = semanticField.getPath().getTail();
        for (int i = 0; i < embeddingLevel && _path != null; i++) {
            _path = _path.getTail();
        }
        return _path;
    }

    @NotNull
    private Optional<Object> findField(final ContentModelData data, FieldPath fieldPath) {
        if (data == null || data.isEmpty() || fieldPath == null) {
            return Optional.empty();
        }

        Object field = data.get(fieldPath.getHead());
        if (fieldPath.hasTail()) {
            if (field instanceof ListWrapper) {
                field = ((ListWrapper) field).get(0);
                log.debug("Field with path {} has multiple values, getting first {}", fieldPath, field);
            }

            return findField((ContentModelData) field, fieldPath.getTail());
        } else {
            return Optional.ofNullable(field);
        }
    }

}

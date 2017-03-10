package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@R2
@Service
@Slf4j
public class SourceConverterFactory {

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private LinkResolver linkResolver;

    private Map<Class<?>, SourceConverter<?>> converters = new HashMap<>();

    @Autowired
    public void setConverters(Set<SourceConverter<?>> sourceConverters) {
        sourceConverters.forEach(sourceConverter ->
                sourceConverter.getTypes().forEach(aClass ->
                        converters.put(aClass, sourceConverter)));
    }

    @NotNull
    public Object convert(Object toConvert, TypeDescriptor targetType, SemanticField semanticField,
                          ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        Class<?> sourceType = toConvert.getClass();

        //typecast is safe which is guaranteed by the fact of a presence of a converter in a collection
        //noinspection unchecked
        return getSourceConverter(sourceType).convert(toConvert, getTypeInformation(targetType), semanticField, pipeline, dataProvider);
    }

    @NotNull
    private SourceConverter getSourceConverter(Class<?> sourceType) throws UnsupportedTargetTypeException {
        SourceConverter sourceConverter = converters.get(sourceType);

        if (sourceConverter == null) {
            log.warn("Cannot get a source converter for {}", sourceType);
            throw new UnsupportedTargetTypeException(sourceType);
        }
        return sourceConverter;
    }

    static TypeInformation getTypeInformation(TypeDescriptor targetType) {
        Class<?> objectType = targetType.getObjectType();

        Class<? extends Collection> collectionType = null;

        if (Collection.class.isAssignableFrom(objectType)) {
            //typecast is safe because of if statement
            //noinspection unchecked
            collectionType = (Class<? extends Collection>) objectType;
            objectType = targetType.getElementTypeDescriptor().getObjectType();
        }

        return TypeInformation.builder()
                .objectType(objectType)
                .collectionType(collectionType)
                .build();
    }

    public Object selfLink(Object toLink, TypeDescriptor targetType, ModelBuilderPipeline pipeline) throws DxaException {
        Class<?> objectType = getClassForSelfLinking(toLink, targetType);

        String itemId = toLink instanceof EntityModelData ? ((EntityModelData) toLink).getId() : ((PageModelData) toLink).getId();
        String url = resolveLink(itemId, webRequestContext, linkResolver);
        if (objectType == String.class) {
            return url;
        } else if (Link.class.isAssignableFrom(objectType)) {
            Link link = new Link();
            link.setUrl(url);
            return link;
        } else if (toLink instanceof EntityModelData && EntityModel.class.isAssignableFrom(objectType)) {
            //type safety is guaranteed by if condition
            //noinspection unchecked
            return pipeline.createEntityModel((EntityModelData) toLink, (Class<EntityModel>) objectType);
        }
        throw new UnsupportedTargetTypeException(objectType);
    }

    @NotNull
    private Class<?> getClassForSelfLinking(Object toLink, TypeDescriptor targetType) throws UnsupportedTargetTypeException {
        Class<?> objectType = targetType.getObjectType();
        Class<?> sourceType = toLink.getClass();

        if (!(PageModelData.class.isAssignableFrom(sourceType) || EntityModelData.class.isAssignableFrom(sourceType))) {
            throw new UnsupportedOperationException("Self-linking is only supported for Entity and Page models, bug have " + sourceType);
        }

        if (!(objectType == String.class || EntityModel.class.isAssignableFrom(objectType))) {
            throw new UnsupportedTargetTypeException(objectType);
        }

        if (PageModelData.class == sourceType && EntityModel.class.isAssignableFrom(objectType)) {
            throw new UnsupportedTargetTypeException(objectType);
        }

        return objectType;
    }

    static String resolveLink(String itemId, WebRequestContext webRequestContext, LinkResolver linkResolver) {
        String publicationId = webRequestContext.getLocalization().getId();
        return linkResolver.resolveLink(TcmUtils.buildTcmUri(publicationId, itemId), publicationId);
    }
}

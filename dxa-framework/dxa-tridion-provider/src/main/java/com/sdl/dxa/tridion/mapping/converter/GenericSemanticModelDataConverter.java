package com.sdl.dxa.tridion.mapping.converter;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.sdl.dxa.tridion.mapping.converter.SemanticModelConverter.getTypeInformation;

/**
 * Implementation capable to convert R2 data model to a semantic entity class fulfilling needs of {@link ModelBuilderPipeline}.
 * Selects specific converter out of set of {@link SemanticModelConverter}s and delegates conversion to it.
 * Uses semantic information and a builder pipeline for conversion.
 */
@Service
@Slf4j
public class GenericSemanticModelDataConverter {

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private LinkResolver linkResolver;

    private Map<Class<?>, SemanticModelConverter<?>> converters = new HashMap<>();

    private static String resolveLink(String itemId, WebRequestContext webRequestContext, LinkResolver linkResolver) {
        String publicationId = webRequestContext.getLocalization().getId();
        String url = TcmUtils.isTcmUri(itemId) ? itemId : TcmUtils.buildTcmUri(publicationId, itemId);
        return linkResolver.resolveLink(url, publicationId);
    }

    @Autowired
    public void setConverters(Set<SemanticModelConverter<?>> semanticModelConverters) {
        semanticModelConverters.forEach(sourceConverter ->
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
    private SemanticModelConverter getSourceConverter(Class<?> sourceType) throws UnsupportedTargetTypeException {
        SemanticModelConverter semanticModelConverter = converters.get(sourceType);

        if (semanticModelConverter == null) {
            log.warn("Cannot get a source converter for {}", sourceType);
            throw new UnsupportedTargetTypeException(sourceType);
        }
        return semanticModelConverter;
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
}

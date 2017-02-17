package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.ExceptionEntity;
import com.sdl.webapp.common.api.model.entity.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.sdl.dxa.tridion.mapping.converter.SourceConverterFactory.resolveLink;

@Component
@Slf4j
public class EntityModelDataConverter implements SourceConverter<EntityModelData> {

    private final SemanticMapper semanticMapper;

    private final LinkResolver linkResolver;

    private final WebRequestContext webRequestContext;

    @Autowired
    public EntityModelDataConverter(SemanticMapper semanticMapper, LinkResolver linkResolver, WebRequestContext webRequestContext) {
        this.semanticMapper = semanticMapper;
        this.linkResolver = linkResolver;
        this.webRequestContext = webRequestContext;
    }

    @Override
    public List<Class<? extends EntityModelData>> getTypes() {
        return Collections.singletonList(EntityModelData.class);
    }

    @Override
    public Object convert(EntityModelData toConvert, TypeDescriptor targetType, SemanticField semanticField, DefaultSemanticFieldDataProvider dataProvider) {
        Class<?> objectType = targetType.getObjectType();

        if (String.class.isAssignableFrom(objectType)) {
            return resolveLink(toConvert.getId(), webRequestContext, linkResolver);
        }

        if (Link.class.isAssignableFrom(objectType)) {
            Link link = new Link();
            link.setUrl(resolveLink(toConvert.getId(), webRequestContext, linkResolver));
            return link;
        }

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

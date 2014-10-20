package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.api.ContentProviderException;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.LinkList;
import org.dd4t.contentmodel.ComponentPresentation;
import org.springframework.stereotype.Component;

@Component
public class LinkListFactory implements EntityFactory {

    private static final Class<?>[] SUPPORTED_ENTITY_TYPES = { LinkList.class };

    @Override
    public Class<?>[] supportedEntityTypes() {
        return SUPPORTED_ENTITY_TYPES;
    }

    @Override
    public Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityType) throws ContentProviderException {
        // TODO: Implement this
        return new LinkList<>();
    }
}

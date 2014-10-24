package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.Notification;
import org.dd4t.contentmodel.ComponentPresentation;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory implements DD4TEntityFactory {

    private static final Class<?>[] SUPPORTED_ENTITY_TYPES = { Notification.class };

    @Override
    public Class<?>[] supportedEntityTypes() {
        return SUPPORTED_ENTITY_TYPES;
    }

    @Override
    public Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityClass) throws ContentProviderException {
        // TODO: Implement this
        return new Notification();
    }
}

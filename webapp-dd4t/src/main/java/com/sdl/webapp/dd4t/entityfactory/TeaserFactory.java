package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.Teaser;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.GenericComponent;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.sdl.webapp.dd4t.entityfactory.FieldUtil.getStringValue;

@Component
public class TeaserFactory implements EntityFactory {

    private static final Class<?>[] SUPPORTED_ENTITY_TYPES = { Teaser.class };

    @Override
    public Class<?>[] supportedEntityTypes() {
        return SUPPORTED_ENTITY_TYPES;
    }

    @Override
    public Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityClass)
            throws ContentProviderException {
        final Teaser teaser = new Teaser();

        final GenericComponent component = componentPresentation.getComponent();
        final Map<String, Field> content = component.getContent();

        // TODO: link, headline, media, text, date, location

        teaser.setHeadline(getStringValue(content, "name"));



        return teaser;
    }
}

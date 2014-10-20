package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.ContentProviderException;
import com.sdl.webapp.common.model.Entity;
import com.sdl.webapp.common.model.entity.Teaser;
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
    public Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityType)
            throws ContentProviderException {
        final Teaser teaser = new Teaser();

        final GenericComponent component = componentPresentation.getComponent();
        final Map<String, Field> content = component.getContent();

        // TODO: link, headline, media, text, date, location

        teaser.setHeadline(getStringValue(content, "name"));



        return teaser;
    }
}

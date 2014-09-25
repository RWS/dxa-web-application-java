package com.sdl.tridion.referenceimpl.dd4t.entityfactory;

import com.sdl.tridion.referenceimpl.common.ContentProviderException;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.entity.YouTubeVideo;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.GenericComponent;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.sdl.tridion.referenceimpl.dd4t.entityfactory.FieldUtil.getFieldStringValue;

@Component
public class YouTubeVideoFactory extends MediaItemFactory {

    private static final Class<?>[] SUPPORTED_ENTITY_TYPES = { YouTubeVideo.class };

    @Override
    public Class<?>[] supportedEntityTypes() {
        return SUPPORTED_ENTITY_TYPES;
    }

    @Override
    public Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityType)
            throws ContentProviderException {
        final YouTubeVideo youTubeVideo = new YouTubeVideo();

        final GenericComponent component = componentPresentation.getComponent();
        final Map<String, Field> componentMetadata = component.getMetadata();

        youTubeVideo.setHeadline(getFieldStringValue(componentMetadata, "headline"));
        youTubeVideo.setYouTubeId(getFieldStringValue(componentMetadata, "youTubeId"));

        return createBaseEntity(componentPresentation, youTubeVideo);
    }
}

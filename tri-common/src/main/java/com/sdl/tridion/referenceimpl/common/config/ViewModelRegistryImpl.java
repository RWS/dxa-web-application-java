package com.sdl.tridion.referenceimpl.common.config;

import com.google.common.collect.ImmutableMap;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.entity.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of {@code ViewModelRegistry}.
 */
@Component
public class ViewModelRegistryImpl implements ViewModelRegistry {

    private static final Map<String, Class<? extends Entity>> ENTITY_VIEW_MODEL_MAP =
            ImmutableMap.<String, Class<? extends Entity>>builder()
                    .put("Article", Article.class)
                    .put("Carousel", ItemList.class)
                    .put("List", ContentList.class)
                    .put("TeaserMap", Teaser.class)
                    .put("YouTubeVideo", YouTubeVideo.class)
                    .build();

    @Override
    public Class<? extends Entity> getEntityViewModelType(String viewName) {
        return ENTITY_VIEW_MODEL_MAP.get(viewName);
    }
}

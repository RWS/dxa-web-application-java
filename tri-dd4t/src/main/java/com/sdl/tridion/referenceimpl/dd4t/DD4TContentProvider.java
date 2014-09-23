package com.sdl.tridion.referenceimpl.dd4t;

import com.sdl.tridion.referenceimpl.common.ContentProvider;
import com.sdl.tridion.referenceimpl.common.PageNotFoundException;
import com.sdl.tridion.referenceimpl.common.model.*;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.entity.ContentList;
import com.sdl.tridion.referenceimpl.common.model.entity.ItemList;
import com.sdl.tridion.referenceimpl.common.model.entity.Teaser;
import com.sdl.tridion.referenceimpl.common.model.entity.YouTubeVideo;
import org.dd4t.contentmodel.*;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.impl.GenericPageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 *
 * TODO: Currently this is a quick-and-dirty implementation. Will be implemented for real in a future sprint.
 */
@Component
public final class DD4TContentProvider implements ContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentProvider.class);

    // TODO: Publication id should be determined from configuration instead of being hard-coded
    private static final int PUBLICATION_ID = 48;

    @Autowired
    private GenericPageFactory pageFactory;

    @Override
    public Page getPage(String uri) throws PageNotFoundException {
        LOG.debug("getPage: uri={}", uri);

        try {
            return createPage(pageFactory.findPageByUrl(uri, PUBLICATION_ID));
        } catch (ItemNotFoundException e) {
            throw new PageNotFoundException("Page not found: " + uri, e);
        }
    }

    private Page createPage(GenericPage genericPage) {
        String viewName = (String) genericPage.getPageTemplate().getMetadata().get("view").getValues().get(0);
        LOG.debug("Page view name: {}", viewName);

        Map<String, List<Entity>> regionData = new LinkedHashMap<>();

        for (ComponentPresentation cp : genericPage.getComponentPresentations()) {
            LOG.debug("Component presentation: {}", cp);

            final ComponentTemplate componentTemplate = cp.getComponentTemplate();
            if (componentTemplate != null) {
                final Map<String, Field> metadata = componentTemplate.getMetadata();
                if (metadata != null) {
                    final Field field = metadata.get("regionView");
                    if (field != null) {
                        final String regionView = (String) field.getValues().get(0);
                        if (!regionData.containsKey(regionView)) {
                            LOG.debug("Creating new region view: {}", regionView);
                            regionData.put(regionView, new ArrayList<Entity>());
                        }

                        regionData.get(regionView).add(createEntity(cp));
                    }
                }
            }
        }

        List<Region> regions = new ArrayList<>();
        for (Map.Entry<String, List<Entity>> entry : regionData.entrySet()) {
            regions.add(RegionImpl.newBuilder().setViewName(entry.getKey()).addEntities(entry.getValue()).build());
        }

        return PageImpl.newBuilder()
                .setId(genericPage.getId())
                .setViewName(viewName)
                .addRegions(regions)
                .build();
    }

    private Entity createEntity(ComponentPresentation cp) {
        final String viewName = (String) cp.getComponentTemplate().getMetadata().get("view").getValues().get(0);

        switch (viewName) {
            case "Carousel":
                return buildCarousel(cp);

            case "TeaserMap":
                return buildTeaserMap(cp);

            case "List":
                return buildContentList(cp);

            case "YouTubeVideo":
                return buildYouTubeVideo(cp);

            default:
                throw new UnsupportedOperationException("Unsupported entity view: " + viewName);
        }
    }

    private ItemList buildCarousel(ComponentPresentation cp) {
        final String id = cp.getComponent().getId();
        final String viewName = (String) cp.getComponentTemplate().getMetadata().get("view").getValues().get(0);

        return ItemList.newBuilder().setId(id).setViewName(viewName).build();
    }

    private Teaser buildTeaserMap(ComponentPresentation cp) {
        final String id = cp.getComponent().getId();
        final String viewName = (String) cp.getComponentTemplate().getMetadata().get("view").getValues().get(0);

        return Teaser.newBuilder()
                .setId(id)
                .setViewName(viewName)
                .setHeadline(getFieldValue(cp, "name"))
                .build();
    }

    private ContentList<Teaser> buildContentList(ComponentPresentation cp) {
        final String id = cp.getComponent().getId();
        final String viewName = (String) cp.getComponentTemplate().getMetadata().get("view").getValues().get(0);

        return ContentList.<Teaser>newBuilder()
                .setId(id)
                .setViewName(viewName)
                .setHeadline(getFieldValue(cp, "headline"))
                .build();
    }

    private YouTubeVideo buildYouTubeVideo(ComponentPresentation cp) {
        final String id = cp.getComponent().getId();
        final String viewName = (String) cp.getComponentTemplate().getMetadata().get("view").getValues().get(0);

        return YouTubeVideo.newBuilder()
                .setId(id)
                .setViewName(viewName)
                .setYouTubeId((String) cp.getComponent().getMetadata().get("youTubeId").getValues().get(0))
                .setHeadline(getMetadataFieldValue(cp, "headline"))
                .build();
    }

    private String getFieldValue(ComponentPresentation cp, String fieldName) {
        final GenericComponent component = cp.getComponent();
        if (component != null) {
            final Map<String, Field> content = component.getContent();
            if (content != null) {
                final Field headlineField = content.get(fieldName);
                if (headlineField != null) {
                    final List<Object> values = headlineField.getValues();
                    if (values != null && values.size() > 0) {
                        final Object value = values.get(0);
                        if (value instanceof String) {
                            return (String) value;
                        }
                    }
                }
            }
        }

        return null;
    }

    private String getMetadataFieldValue(ComponentPresentation cp, String fieldName) {
        final GenericComponent component = cp.getComponent();
        if (component != null) {
            final Map<String, Field> metadata = component.getMetadata();
            if (metadata != null) {
                final Field headlineField = metadata.get(fieldName);
                if (headlineField != null) {
                    final List<Object> values = headlineField.getValues();
                    if (values != null && values.size() > 0) {
                        final Object value = values.get(0);
                        if (value instanceof String) {
                            return (String) value;
                        }
                    }
                }
            }
        }

        return null;
    }
}

package com.sdl.tridion.referenceimpl.dd4t;

import com.sdl.tridion.referenceimpl.common.ContentProvider;
import com.sdl.tridion.referenceimpl.common.PageNotFoundException;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.impl.GenericPageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
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
            return buildPage(pageFactory.findPageByUrl(uri, PUBLICATION_ID));
        } catch (ItemNotFoundException e) {
            throw new PageNotFoundException("Page not found: " + uri, e);
        }
    }

    private Page buildPage(GenericPage genericPage) {
        // TODO: Error checking, fallback if metadata field not found, etc.
        String viewName = (String) genericPage.getPageTemplate().getMetadata().get("view").getValues().get(0);
        LOG.debug("Page view name: {}", viewName);

        Map<String, Region.Builder> regionBuilders = new LinkedHashMap<>();

        // TODO: This is quick and dirty. See C# version: DD4TModelBuilder.CreatePage on how to do this properly.
        for (ComponentPresentation cp : genericPage.getComponentPresentations()) {
            LOG.debug("Component presentation: {}", cp);

            final ComponentTemplate componentTemplate = cp.getComponentTemplate();
            if (componentTemplate != null) {
                final Map<String, Field> metadata = componentTemplate.getMetadata();
                if (metadata != null) {
                    final Field field = metadata.get("regionView");
                    if (field != null) {
                        final String regionView = (String) field.getValues().get(0);
                        if (!regionBuilders.containsKey(regionView)) {
                            LOG.debug("Creating new region view: {}", regionView);
                            regionBuilders.put(regionView, Region.newBuilder().setViewName(regionView));
                        }

                        String entityView = (String) cp.getComponentTemplate().getMetadata().get("view").getValues().get(0);
                        LOG.debug("entityView={}", entityView);

                        regionBuilders.get(regionView).addEntity(
                                Entity.newBuilder()
                                        .setId(cp.getComponent().getId())
                                        .setViewName(entityView)
                                        .build());
                    }
                }
            }
        }

        List<Region> regions = new ArrayList<>();
        for (Region.Builder regionBuilder : regionBuilders.values()) {
            regions.add(regionBuilder.build());
        }

        return Page.newBuilder()
                .setId(genericPage.getId())
                .setTitle(genericPage.getTitle())
                .setViewName(viewName)
                .addRegions(regions)
                .build();
    }
}

package com.sdl.tridion.referenceimpl.dd4t;

import com.sdl.tridion.referenceimpl.common.ContentProvider;
import com.sdl.tridion.referenceimpl.common.PageNotFoundException;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.common.model.impl.EntityImpl;
import com.sdl.tridion.referenceimpl.common.model.impl.PageImpl;
import com.sdl.tridion.referenceimpl.common.model.impl.RegionImpl;
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

        Map<String, List<Entity>> regionData = new LinkedHashMap<>();

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
                        if (!regionData.containsKey(regionView)) {
                            LOG.debug("Creating new region view: {}", regionView);
                            regionData.put(regionView, new ArrayList<Entity>());
                        }

                        String entityView = (String) cp.getComponentTemplate().getMetadata().get("view").getValues().get(0);
                        LOG.debug("entityView={}", entityView);

                        regionData.get(regionView).add(new EntityImpl(cp.getComponent().getId(), entityView));
                    }
                }
            }
        }

        List<Region> regions = new ArrayList<>();
        for (Map.Entry<String, List<Entity>> entry : regionData.entrySet()) {
            regions.add(new RegionImpl(entry.getKey(), entry.getValue()));
        }

        return new PageImpl(genericPage.getId(), viewName, regions);
    }
}

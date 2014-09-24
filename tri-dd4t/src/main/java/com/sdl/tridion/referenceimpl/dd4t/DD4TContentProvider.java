package com.sdl.tridion.referenceimpl.dd4t;

import com.google.common.base.Strings;
import com.sdl.tridion.referenceimpl.common.ContentProvider;
import com.sdl.tridion.referenceimpl.common.ContentProviderException;
import com.sdl.tridion.referenceimpl.common.PageNotFoundException;
import com.sdl.tridion.referenceimpl.common.config.ViewModelRegistry;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.common.model.entity.EntityBase;
import com.sdl.tridion.referenceimpl.common.model.page.PageImpl;
import com.sdl.tridion.referenceimpl.common.model.region.RegionImpl;
import org.dd4t.contentmodel.*;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.impl.GenericPageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
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

    private static final String PAGE_VIEW_PREFIX = "core/page/";
    private static final String REGION_VIEW_PREFIX = "core/region/";
    private static final String ENTITY_VIEW_PREFIX = "core/entity/";

    @Autowired
    private GenericPageFactory pageFactory;

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Override
    public Page getPage(String url) throws ContentProviderException {
        final GenericPage genericPage;
        try {
            genericPage = pageFactory.findPageByUrl(url, PUBLICATION_ID);
        } catch (ItemNotFoundException e) {
            throw new PageNotFoundException("Page not found: " + url, e);
        }

        return createPage(genericPage);
    }

    private Page createPage(GenericPage genericPage) throws ContentProviderException {
        PageImpl page = new PageImpl();

        page.setId(genericPage.getId());

        Map<String, RegionImpl> regions = new HashMap<>();
        Map<String, Entity> entities = new HashMap<>();

        for (ComponentPresentation cp : genericPage.getComponentPresentations()) {
            final GenericComponent component = cp.getComponent();
            final String componentId = component.getId();
            LOG.debug("componentId={}", componentId);

            Entity entity = entities.get(componentId);
            if (entity == null) {
                entity = createEntity(cp);
                if (entity == null) {
                    LOG.warn("Failed to create entity for component: {}", componentId);
                    continue;
                }

                entities.put(componentId, entity);
            } else {
                LOG.warn("Duplicate entity found: {}", componentId);
            }

            final Map<String, Field> templateMeta = cp.getComponentTemplate().getMetadata();
            if (templateMeta != null) {
                final String regionViewName = getFieldStringValue(templateMeta, "regionView");
                if (!Strings.isNullOrEmpty(regionViewName)) {
                    RegionImpl region = regions.get(regionViewName);
                    if (region == null) {
                        LOG.debug("Creating region: {}", regionViewName);
                        region = new RegionImpl();

                        region.setName(regionViewName);
                        region.setModule("core");
                        region.setViewName(REGION_VIEW_PREFIX + regionViewName);

                        regions.put(regionViewName, region);
                    }

                    region.getEntities().add(entity);
                }
            }
        }

        Map<String, Region> regionMap = new HashMap<>();
        regionMap.putAll(regions);
        page.setRegions(regionMap);

        page.setEntities(entities);

        page.setViewName(PAGE_VIEW_PREFIX + getPageViewName(genericPage));

        return page;
    }

    private Entity createEntity(ComponentPresentation cp) throws ContentProviderException {
        final Map<String, Field> templateMeta = cp.getComponentTemplate().getMetadata();
        if (templateMeta != null) {
            final String viewName = getFieldStringValue(templateMeta, "view");
            LOG.debug("View for component {}: {} ", cp.getComponent().getId(), viewName);

            final Class<? extends Entity> entityType = viewModelRegistry.getEntityViewModelType(viewName);
            if (entityType == null) {
                throw new ContentProviderException("Cannot determine entity type for view name: " + viewName);
            }

            final Entity entity;
            try {
                LOG.debug("Creating entity of type: {}", entityType.getName());
                entity = entityType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ContentProviderException("Error while creating instance of entity of type: " +
                        entityType.getName(), e);
            }

            // TODO: Fill entity fields via reflection (mapping from XML to entity fields)

            ReflectionUtils.doWithFields(entityType, new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(java.lang.reflect.Field field) throws IllegalArgumentException, IllegalAccessException {
                    LOG.debug("field={}", field.getName());
                }
            });

            ((EntityBase) entity).setId(cp.getComponent().getId());
            ((EntityBase) entity).setViewName(ENTITY_VIEW_PREFIX + viewName);

            return entity;
        }

        return null;
    }

    private String getPageViewName(GenericPage genericPage) {
        final PageTemplate pageTemplate = genericPage.getPageTemplate();
        if (pageTemplate != null) {
            return getFieldStringValue(pageTemplate.getMetadata(), "view");
        }

        return null;
    }

    private String getFieldStringValue(Map<String, Field> fields, String name) {
        if (fields != null) {
            final Field field = fields.get(name);
            if (field != null) {
                final List<Object> values = field.getValues();
                if (values != null && !values.isEmpty()) {
                    return (String) values.get(0);
                }
            }
        }

        return null;
    }
}

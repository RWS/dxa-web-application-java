package com.sdl.tridion.referenceimpl.dd4t;

import com.google.common.base.Strings;
import com.sdl.tridion.referenceimpl.common.ContentProvider;
import com.sdl.tridion.referenceimpl.common.ContentProviderException;
import com.sdl.tridion.referenceimpl.common.PageNotFoundException;
import com.sdl.tridion.referenceimpl.common.config.Localization;
import com.sdl.tridion.referenceimpl.common.config.ViewModelRegistry;
import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.common.model.entity.ContentList;
import com.sdl.tridion.referenceimpl.common.model.entity.EntityBase;
import com.sdl.tridion.referenceimpl.common.model.entity.Teaser;
import com.sdl.tridion.referenceimpl.common.model.page.PageImpl;
import com.sdl.tridion.referenceimpl.common.model.region.RegionImpl;
import com.sdl.tridion.referenceimpl.dd4t.entityfactory.EntityFactoryRegistry;
import org.dd4t.contentmodel.*;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.factories.PageFactory;
import org.dd4t.core.filters.FilterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.sdl.tridion.referenceimpl.dd4t.entityfactory.FieldUtil.getStringValue;

/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 */
@Component
public final class DD4TContentProvider implements ContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentProvider.class);

    private static final String CORE_MODULE_NAME = "core";

    private static final String PAGE_VIEW_PREFIX = CORE_MODULE_NAME + "/page/";
    private static final String REGION_VIEW_PREFIX = CORE_MODULE_NAME + "/region/";
    private static final String ENTITY_VIEW_PREFIX = CORE_MODULE_NAME + "/entity/";

    private static final String DEFAULT_PAGE_NAME = "index.html";
    private static final String DEFAULT_PAGE_EXTENSION = ".html";

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private EntityFactoryRegistry entityFactoryRegistry;

    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    public Page getPage(String url) throws ContentProviderException {
        String processedUrl = processUrl(url);
        GenericPage genericPage = tryFindPageByUrl(processedUrl, webRequestContext.getPublicationId());
        if (genericPage == null && !url.endsWith("/") && url.lastIndexOf('.') <= url.lastIndexOf('/')) {
            processedUrl = processUrl(url + "/");
            genericPage = tryFindPageByUrl(processedUrl, webRequestContext.getPublicationId());
            if (genericPage == null) {
                throw new PageNotFoundException("Page not found: " + processedUrl);
            }
        }

        return createPage(genericPage);
    }

    private String processUrl(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return DEFAULT_PAGE_NAME;
        }

        if (url.endsWith("/")) {
            url = url + DEFAULT_PAGE_NAME;
        }

        // If the URL has no extension, then add the default extension
        if (url.lastIndexOf('.') <= url.lastIndexOf('/')) {
            url = url + DEFAULT_PAGE_EXTENSION;
        }

        return url;
    }

    private GenericPage tryFindPageByUrl(String url, int publicationId) {
        try {
            LOG.debug("tryFindPageByUrl: url={}, publicationId = {}", url, publicationId);
            return (GenericPage)pageFactory.findPageByUrl(url, publicationId);
        } catch (FilterException| SerializationException | ParseException | ItemNotFoundException | IOException e) {
	        LOG.error(e.getMessage(),e);
            return null;
        }
    }

    private Page createPage(GenericPage genericPage) throws ContentProviderException {
        PageImpl page = new PageImpl();

        page.setId(genericPage.getId());
        page.setTitle(genericPage.getTitle());

        // Get and add includes
        final String pageTypeId = genericPage.getPageTemplate().getId().split("-")[1];
        final Localization localization = webRequestContext.getLocalization();
        for (String include : localization.getIncludes(pageTypeId)) {
            final String includeUrl = localization.getPath() + "/" + include;
            final Page includePage = getPage(includeUrl);
            page.getIncludes().put(includePage.getTitle(), includePage);
        }

        Map<String, RegionImpl> regions = new LinkedHashMap<>();

        for (ComponentPresentation cp : genericPage.getComponentPresentations()) {
            final Entity entity = createEntity(cp);

            final Map<String, Field> templateMeta = cp.getComponentTemplate().getMetadata();
            if (templateMeta != null) {
                final String regionName = getStringValue(templateMeta, "regionView");
                if (!Strings.isNullOrEmpty(regionName)) {
                    RegionImpl region = regions.get(regionName);
                    if (region == null) {
                        LOG.debug("Creating region: {}", regionName);
                        region = new RegionImpl();

                        region.setName(regionName);
                        region.setModule(CORE_MODULE_NAME);
                        region.setViewName(REGION_VIEW_PREFIX + regionName);

                        regions.put(regionName, region);
                    }

                    region.getEntities().add(entity);
                }
            }
        }

        Map<String, Region> regionMap = new HashMap<>();
        regionMap.putAll(regions);
        page.setRegions(regionMap);

        final String pageViewName = getPageViewName(genericPage);
        page.setViewName(PAGE_VIEW_PREFIX + pageViewName);

        return page;
    }

    private String getEntityIdFromComponentId(String componentId) {
        return componentId.split("-")[1];
    }

    private Entity createEntity(ComponentPresentation cp) throws ContentProviderException {
        final GenericComponent component = cp.getComponent();

        final Map<String, Field> templateMeta = cp.getComponentTemplate().getMetadata();
        if (templateMeta != null) {
            final String componentId = component.getId();

            final String viewName = getStringValue(templateMeta, "view");
            LOG.debug("{}: viewName: {}", componentId, viewName);

            final Class<? extends Entity> entityType = viewModelRegistry.getEntityViewModelType(viewName);
            if (entityType == null) {
                throw new ContentProviderException("Cannot determine entity type for view name: " + viewName +
                        "\nPlease make sure that an entry is registered for this view name in the ViewModelRegistry.");
            }

            LOG.debug("{}: Creating entity of type: {}", componentId, entityType.getName());
            EntityBase entity = (EntityBase) entityFactoryRegistry.getFactoryFor(entityType).createEntity(cp, entityType);

            entity.setId(getEntityIdFromComponentId(componentId));
            entity.setViewName(ENTITY_VIEW_PREFIX + viewName);

            return entity;
        }

        return null;
    }

    private String getPageViewName(GenericPage genericPage) {
        final PageTemplate pageTemplate = genericPage.getPageTemplate();
        if (pageTemplate != null) {
            return getStringValue(pageTemplate.getMetadata(), "view");
        }

        return null;
    }

    public void populateDynamicList(ContentList<Teaser> list) {
//        BrokerQuery brokerQuery = new BrokerQuery();
//
//        brokerQuery.setStart(list.getStart());
//        brokerQuery.setPublicationId(webRequestContext.getLocalization().getPublicationId());
//        brokerQuery.setPageSize(list.getPageSize());
//        brokerQuery.setSchemaId(mapSchema(list.getContentType().getKey()));
//        brokerQuery.setSort(list.getSort().getKey());
//
//        list.setItemListElements(brokerQuery.executeQuery());
//        list.setHasMore(brokerQuery.isHasMore());
//
//        for (Teaser teaser : list.getItemListElements()) {
//            String url = teaser.getLink().getUrl(); //TODO modified to include ContentResolver.ResolveLink();
//
//            Link link = teaser.getLink();
//            link.setUrl(url);
//
//            teaser.setLink(link);
//        }
    }

    protected int mapSchema(String schemaKey) {
        String[] bits = schemaKey.split(".");

        String moduleName = bits.length > 1 ? bits[0] : CORE_MODULE_NAME;
        schemaKey = bits.length > 1 ? bits[1] : bits[0];

        int res;
        String schemaId = webRequestContext.getLocalization().getConfiguration(String.format("{0}.schemas.{1}",moduleName, schemaKey));

        try {
            res = Integer.parseInt(schemaId);
        } catch (NumberFormatException nfe) {
            res = 0; //.Net does a TryParse() which returns fall is failed, we return 0 as fallback.
        }

        return res;
    }
}

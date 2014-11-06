package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.page.PageImpl;
import com.sdl.webapp.common.api.model.region.RegionImpl;
import com.sdl.webapp.dd4t.fieldconverters.FieldConverterRegistry;
import org.dd4t.contentmodel.*;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.filters.FilterException;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.sdl.webapp.dd4t.fieldconverters.FieldUtils.getStringValue;

/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 */
@Component
public final class DD4TContentProvider implements ContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentProvider.class);

    private static final String DEFAULT_PAGE_NAME = "index.html";
    private static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static final String DEFAULT_REGION_NAME = "Main";

    private static final String CORE_MODULE_NAME = "core";

    private static final String PAGE_VIEW_PREFIX = CORE_MODULE_NAME + "/page/";
    private static final String REGION_VIEW_PREFIX = CORE_MODULE_NAME + "/region/";
    private static final String ENTITY_VIEW_PREFIX = CORE_MODULE_NAME + "/entity/";

    private static interface TryFindPage<T> {
        public T tryFindPage(String url, int publicationId) throws ContentProviderException;
    }

    private final org.dd4t.core.factories.PageFactory dd4tPageFactory;

    private final ViewModelRegistry viewModelRegistry;

    private final SemanticMapper semanticMapper;

    private final FieldConverterRegistry fieldConverterRegistry;

    @Autowired
    public DD4TContentProvider(org.dd4t.core.factories.PageFactory dd4tPageFactory, ViewModelRegistry viewModelRegistry,
                               SemanticMapper semanticMapper, FieldConverterRegistry fieldConverterRegistry) {
        this.dd4tPageFactory = dd4tPageFactory;
        this.viewModelRegistry = viewModelRegistry;
        this.semanticMapper = semanticMapper;
        this.fieldConverterRegistry = fieldConverterRegistry;
    }

    @Override
    public Page getPageModel(String url, final Localization localization) throws ContentProviderException {
        return findPage(url, localization, new TryFindPage<Page>() {
            @Override
            public Page tryFindPage(String url, int publicationId) throws ContentProviderException {
                GenericPage genericPage;
                try {
                    genericPage = (GenericPage) dd4tPageFactory.findPageByUrl(url, publicationId);
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, url);
                    return null;
                } catch (FilterException | ParseException | SerializationException | IOException e) {
                    throw new ContentProviderException("Exception while getting page model for: [" +
                            publicationId + "] " + url, e);
                }

                return createPage(genericPage, localization);
            }
        });
    }

    @Override
    public InputStream getPageContent(String url, Localization localization) throws ContentProviderException {
        return findPage(url, localization, new TryFindPage<InputStream>() {
            @Override
            public InputStream tryFindPage(String url, int publicationId) throws ContentProviderException {
                final String pageContent;
                try {
                    // NOTE: The method is called 'findXMLPageByUrl' but it does actually not have anything to do with XML
                    pageContent = dd4tPageFactory.findXMLPageByUrl(url, publicationId);
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, url);
                    return null;
                } catch (FilterException | ParseException | SerializationException | IOException e) {
                    throw new ContentProviderException("Exception while getting page content for: [" +
                            publicationId + "] " + url, e);
                }

                // NOTE: This assumes page content is always in UTF-8 encoding
                return new ByteArrayInputStream(pageContent.getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    private static <T> T findPage(String url, Localization localization, TryFindPage<T> callback)
            throws ContentProviderException {
        String processedUrl = processUrl(url);
        int publicationId = Integer.parseInt(localization.getId());

        LOG.debug("Try to find page: [{}] {}", publicationId, processedUrl);
        T page = callback.tryFindPage(processedUrl, publicationId);
        if (page == null && !url.endsWith("/") && !hasExtension(url)) {
            processedUrl = processUrl(url + "/");
            LOG.debug("Try to find page: [{}] {}", publicationId, processedUrl);
            page = callback.tryFindPage(processedUrl, publicationId);
        }

        if (page == null) {
            throw new PageNotFoundException("Page not found: [" + publicationId + "] " + processedUrl);
        }

        return page;
    }

    private static String processUrl(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return DEFAULT_PAGE_NAME;
        }
        if (url.endsWith("/")) {
            url = url + DEFAULT_PAGE_NAME;
        }
        if (!hasExtension(url)) {
            url = url + DEFAULT_PAGE_EXTENSION;
        }
        return url;
    }

    private static boolean hasExtension(String url) {
        return url.lastIndexOf('.') > url.lastIndexOf('/');
    }

    private Page createPage(GenericPage genericPage, Localization localization) throws ContentProviderException {
        final PageImpl page = new PageImpl();

        page.setId(genericPage.getId());
        page.setTitle(genericPage.getTitle());

        String localizationPath = localization.getPath();
        if (!localizationPath.endsWith("/")) {
            localizationPath = localizationPath + "/";
        }

        // Get and add includes
        final String pageTypeId = genericPage.getPageTemplate().getId().split("-")[1];
        for (String include : localization.getIncludes(pageTypeId)) {
            final String includeUrl = localizationPath + include;
            final Page includePage = getPageModel(includeUrl, localization);
            page.getIncludes().put(includePage.getTitle(), includePage);
        }

        final Map<String, RegionImpl> regions = new LinkedHashMap<>();

        for (ComponentPresentation cp : genericPage.getComponentPresentations()) {
            final Entity entity = createEntity(cp, localization);

            final Map<String, Field> templateMeta = cp.getComponentTemplate().getMetadata();
            if (templateMeta != null) {
                String regionName = getStringValue(templateMeta, "regionView");
                if (Strings.isNullOrEmpty(regionName)) {
                    regionName = DEFAULT_REGION_NAME;
                }

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

        final Map<String, Region> regionMap = new LinkedHashMap<>();
        regionMap.putAll(regions);
        page.setRegions(regionMap);

        page.setViewName(PAGE_VIEW_PREFIX + getPageViewName(genericPage));

        return page;
    }

    private Entity createEntity(ComponentPresentation cp, Localization localization) throws ContentProviderException {
        final GenericComponent component = cp.getComponent();
        final ComponentTemplate componentTemplate = cp.getComponentTemplate();

        final Map<String, Field> templateMeta = componentTemplate.getMetadata();
        if (templateMeta != null) {
            final String componentId = component.getId();

            final String viewName = getStringValue(templateMeta, "view");
            LOG.debug("{}: viewName: {}", componentId, viewName);

            final Class<? extends Entity> entityClass = viewModelRegistry.getViewEntityClass(viewName);
            if (entityClass == null) {
                throw new ContentProviderException("Cannot determine entity type for view name: '" + viewName +
                        "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
            }

            LOG.debug("{}: Creating entity of type: {}", componentId, entityClass.getName());

            final SemanticSchema semanticSchema = localization.getSemanticSchemas().get(
                    Long.parseLong(component.getSchema().getId().split("-")[1]));

            final AbstractEntity entity;
            try {
                entity = (AbstractEntity) semanticMapper.createEntity(entityClass, semanticSchema.getSemanticFields(),
                        new DD4TSemanticFieldDataProvider(component, fieldConverterRegistry));
            } catch (SemanticMappingException e) {
                throw new ContentProviderException(e);
            }

            entity.setId(componentId.split("-")[1]);
            entity.setViewName(ENTITY_VIEW_PREFIX + viewName);

            // Set entity data (used for semantic markup)
            ImmutableMap.Builder<String, String> entityDataBuilder = ImmutableMap.builder();
            entityDataBuilder.put("ComponentID", componentId);
            entityDataBuilder.put("ComponentModified",
                    ISODateTimeFormat.dateHourMinuteSecond().print(component.getRevisionDate()));
            entityDataBuilder.put("ComponentTemplateID", componentTemplate.getId());
            entityDataBuilder.put("ComponentTemplateModified",
                    ISODateTimeFormat.dateHourMinuteSecond().print(componentTemplate.getRevisionDate()));
            entity.setEntityData(entityDataBuilder.build());

            // Special handling for media items
            if (entity instanceof MediaItem && component.getMultimedia() != null &&
                    !Strings.isNullOrEmpty(component.getMultimedia().getUrl())) {
                final Multimedia multimedia = component.getMultimedia();
                final MediaItem mediaItem = (MediaItem) entity;
                mediaItem.setUrl(multimedia.getUrl());
                mediaItem.setFileName(multimedia.getFileName());
                mediaItem.setFileSize(multimedia.getSize());
                mediaItem.setMimeType(multimedia.getMimeType());
            }

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
}

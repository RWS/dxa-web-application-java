package com.sdl.webapp.dd4t;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.*;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.page.PageImpl;
import com.sdl.webapp.common.api.model.region.RegionImpl;
import com.sdl.webapp.dd4t.fieldconverters.FieldConverterRegistry;
import org.dd4t.contentmodel.*;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.core.factories.PageFactory;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.resolvers.LinkResolver;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sdl.webapp.dd4t.fieldconverters.FieldUtils.getStringValue;

/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 *
 * TODO: Needs to be refactored, this is getting too big, messy and complicated.
 */
@Component
public final class DD4TContentProvider implements ContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentProvider.class);

    private static final String DEFAULT_PAGE_NAME = "index.html";
    private static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static final String DEFAULT_REGION_NAME = "Main";

    private static final String REGION_FOR_PAGE_TITLE_COMPONENT = "Main";
    private static final String STANDARD_METADATA_FIELD_NAME = "standardMeta";
    private static final String STANDARD_METADATA_TITLE_FIELD_NAME = "name";
    private static final String STANDARD_METADATA_DESCRIPTION_FIELD_NAME = "description";
    private static final String COMPONENT_PAGE_TITLE_FIELD_NAME = "headline";

    private static final Pattern VIEW_NAME_PATTERN = Pattern.compile(".*\\[(.*)\\]");

    private static interface TryFindPage<T> {
        public T tryFindPage(String url, int publicationId) throws ContentProviderException;
    }

    private final org.dd4t.core.factories.PageFactory dd4tPageFactory;

    private final LinkResolver linkResolver;

    private final ViewModelRegistry viewModelRegistry;

    private final SemanticMapper semanticMapper;

    private final FieldConverterRegistry fieldConverterRegistry;

    private final WebRequestContext webRequestContext;

    @Autowired
    public DD4TContentProvider(PageFactory dd4tPageFactory, LinkResolver linkResolver, ViewModelRegistry viewModelRegistry,
                               SemanticMapper semanticMapper, FieldConverterRegistry fieldConverterRegistry,
                               WebRequestContext webRequestContext) {
        this.dd4tPageFactory = dd4tPageFactory;
        this.linkResolver = linkResolver;
        this.viewModelRegistry = viewModelRegistry;
        this.semanticMapper = semanticMapper;
        this.fieldConverterRegistry = fieldConverterRegistry;
        this.webRequestContext = webRequestContext;
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
                    pageContent = dd4tPageFactory.findPageContentByUrl(url, publicationId);
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
        page.setName(genericPage.getTitle()); // It's confusing, but what DD4T calls the "title" is what is called the "name" here

        final Map<String, String> pageMeta = new HashMap<>();
        final String title = processPageMetadata(genericPage, pageMeta, localization);
        page.setTitle(title);
        page.setMeta(pageMeta);

        String localizationPath = localization.getPath();
        if (!localizationPath.endsWith("/")) {
            localizationPath = localizationPath + "/";
        }

        // Get and add includes
        final String pageTypeId = genericPage.getPageTemplate().getId().split("-")[1];
        for (String include : localization.getIncludes(pageTypeId)) {
            final String includeUrl = localizationPath + include;
            final Page includePage = getPageModel(includeUrl, localization);
            page.getIncludes().put(includePage.getName(), includePage);
        }

        final Map<String, RegionImpl> regions = new LinkedHashMap<>();

        for (ComponentPresentation cp : genericPage.getComponentPresentations()) {
            final Entity entity = createEntity(cp, localization);

            String regionName = getRegionName(cp);
            if (!Strings.isNullOrEmpty(regionName)) {
                RegionImpl region = regions.get(regionName);
                if (region == null) {
                    LOG.debug("Creating region: {}", regionName);
                    region = new RegionImpl();

                    region.setName(regionName);
                    region.setMvcData(createRegionMvcData(cp));

                    regions.put(regionName, region);
                }

                region.getEntities().add(entity);
            }
        }

        final Map<String, Region> regionMap = new LinkedHashMap<>();
        regionMap.putAll(regions);
        page.setRegions(regionMap);

        page.setPageData(getPageData(genericPage, localization));

        page.setMvcData(createPageMvcData(genericPage));

        return page;
    }

    private String processPageMetadata(GenericPage page, Map<String, String> pageMeta, Localization localization) {
        // Process page metadata fields
        if (page.getMetadata() != null) {
            for (Field field : page.getMetadata().values()) {
                processMetadataField(field, pageMeta);
            }
        }

        String description = pageMeta.get("description");
        String title = pageMeta.get("title");
        String image = pageMeta.get("image");

        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(description)) {
            for (ComponentPresentation cp : page.getComponentPresentations()) {
                if (REGION_FOR_PAGE_TITLE_COMPONENT.equals(getRegionName(cp))) {
                    final GenericComponent component = cp.getComponent();

                    final Map<String, Field> metadata = component.getMetadata();
                    BaseField standardMetaField = (BaseField) metadata.get(STANDARD_METADATA_FIELD_NAME);
                    if (standardMetaField != null && !standardMetaField.getEmbeddedValues().isEmpty()) {
                        final Map<String, Field> standardMeta = standardMetaField.getEmbeddedValues().get(0).getContent();
                        if (Strings.isNullOrEmpty(title) && standardMeta.containsKey(STANDARD_METADATA_TITLE_FIELD_NAME)) {
                            title = standardMeta.get(STANDARD_METADATA_TITLE_FIELD_NAME).getValues().get(0).toString();
                        }
                        if (Strings.isNullOrEmpty(description) && standardMeta.containsKey(STANDARD_METADATA_DESCRIPTION_FIELD_NAME)) {
                            description = standardMeta.get(STANDARD_METADATA_DESCRIPTION_FIELD_NAME).getValues().get(0).toString();
                        }
                    }

                    final Map<String, Field> content = component.getContent();
                    if (Strings.isNullOrEmpty(title) && content.containsKey(COMPONENT_PAGE_TITLE_FIELD_NAME)) {
                        title = content.get(COMPONENT_PAGE_TITLE_FIELD_NAME).getValues().get(0).toString();
                    }

                    if (Strings.isNullOrEmpty(image) && content.containsKey("image")) {
                        image = ((GenericComponent) ((BaseField) content.get("image")).getLinkedComponentValues().get(0))
                                .getMultimedia().getUrl();
                    }

                    break;
                }
            }
        }

        // Use page title if no title found
        if (Strings.isNullOrEmpty(title)) {
            title = page.getTitle().replace("^\\d(3)\\s", "");
            if (title.equalsIgnoreCase("index") || title.equalsIgnoreCase("default")) {
                // Use default page title from configuration if nothing better was found
                title = localization.getResource("core.defaultPageTitle");
            }
        }

        pageMeta.put("twitter:card", "summary");
        pageMeta.put("og:title", title);
        pageMeta.put("og:url", webRequestContext.getRequestUrl());
        pageMeta.put("og:type", "article");
        pageMeta.put("og:locale", localization.getCulture());

        if (!Strings.isNullOrEmpty(description)) {
            pageMeta.put("og:description", description);
        }

        if (!Strings.isNullOrEmpty(image)) {
            pageMeta.put("og:image", webRequestContext.getBaseUrl() + image);
        }

        if (!pageMeta.containsKey("description")) {
            pageMeta.put("description", !Strings.isNullOrEmpty(description) ? description : title);
        }

        return title + " " + localization.getResource("core.pageTitleSeparator") + " " +
                localization.getResource("core.pageTitlePostfix");
    }

    private void processMetadataField(Field field, Map<String, String> pageMeta) {
        // If it's an embedded field, then process the subfields
        if (field.getFieldType() == FieldType.Embedded) {
            final List<FieldSet> embeddedValues = ((BaseField) field).getEmbeddedValues();
            if (embeddedValues != null && !embeddedValues.isEmpty()) {
                for (Field subfield : embeddedValues.get(0).getContent().values()) {
                    processMetadataField(subfield, pageMeta);
                }
            }
        } else {
            final String fieldName = field.getName();

            String value;
            switch (fieldName) {
                case "internalLink":
                    final String componentId = field.getValues().get(0).toString();
                    try {
                        value = linkResolver.resolve(componentId);
                    } catch (SerializationException | ItemNotFoundException e) {
                        LOG.warn("Error while resolving link: {}", componentId);
                        value = componentId;
                    }
                    break;
                case "image":
                    value = ((GenericComponent) ((BaseField) field).getLinkedComponentValues().get(0))
                            .getMultimedia().getUrl();
                    break;
                default:
                    value = Joiner.on(',').join(field.getValues());
                    break;
            }

            if (!Strings.isNullOrEmpty(value) && !pageMeta.containsKey(fieldName)) {
                pageMeta.put(fieldName, value);
            }
        }
    }

    private String getRegionName(ComponentPresentation cp) {
        final Map<String, Field> templateMeta = cp.getComponentTemplate().getMetadata();
        if (templateMeta != null) {
            String regionName = getStringValue(templateMeta, "regionView");
            if (Strings.isNullOrEmpty(regionName)) {
                regionName = DEFAULT_REGION_NAME;
            }
            return regionName;
        }

        return null;
    }

    private Map<String, String> getPageData(GenericPage page, Localization localization) {
        final PageTemplate pageTemplate = page.getPageTemplate();

        ImmutableMap.Builder<String, String> pageDataBuilder = ImmutableMap.builder();
        pageDataBuilder.put("PageID", page.getId());
        pageDataBuilder.put("PageModified", ISODateTimeFormat.dateHourMinuteSecond().print(page.getRevisionDate()));
        pageDataBuilder.put("PageTemplateID", pageTemplate.getId());
        pageDataBuilder.put("PageTemplateModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(pageTemplate.getRevisionDate()));

        pageDataBuilder.put("CmsUrl", localization.getConfiguration("core.cmsurl"));

        return pageDataBuilder.build();
    }

    private Entity createEntity(ComponentPresentation cp, Localization localization) throws ContentProviderException {
        final GenericComponent component = cp.getComponent();
        final ComponentTemplate componentTemplate = cp.getComponentTemplate();

        final Map<String, Field> templateMeta = componentTemplate.getMetadata();
        if (templateMeta != null) {
            final String componentId = component.getId();

            final String viewName = getStringValue(templateMeta, "view");
            LOG.debug("{}: viewName: {}", componentId, viewName);

            final Class<? extends AbstractEntity> entityClass = viewModelRegistry.getViewEntityClass(viewName);
            if (entityClass == null) {
                throw new ContentProviderException("Cannot determine entity type for view name: '" + viewName +
                        "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
            }

            LOG.debug("{}: Creating entity of type: {}", componentId, entityClass.getName());

            final SemanticSchema semanticSchema = localization.getSemanticSchemas().get(
                    Long.parseLong(component.getSchema().getId().split("-")[1]));

            final AbstractEntity entity;
            try {
                entity = semanticMapper.createEntity(entityClass, semanticSchema.getSemanticFields(),
                        new DD4TSemanticFieldDataProvider(component, fieldConverterRegistry));
            } catch (SemanticMappingException e) {
                throw new ContentProviderException(e);
            }

            entity.setId(componentId.split("-")[1]);
            entity.setMvcData(createEntityMvcData(cp));

            // Set entity data (used for semantic markup)
            entity.setEntityData(getEntityData(cp));

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

    private Map<String, String> getEntityData(ComponentPresentation cp) {
        final GenericComponent component = cp.getComponent();
        final ComponentTemplate componentTemplate = cp.getComponentTemplate();

        ImmutableMap.Builder<String, String> entityDataBuilder = ImmutableMap.builder();
        entityDataBuilder.put("ComponentID", component.getId());
        entityDataBuilder.put("ComponentModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(component.getRevisionDate()));
        entityDataBuilder.put("ComponentTemplateID", componentTemplate.getId());
        entityDataBuilder.put("ComponentTemplateModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(componentTemplate.getRevisionDate()));

        return entityDataBuilder.build();
    }

    private MvcData createPageMvcData(GenericPage genericPage) {
        final DD4TMvcData mvcData = new DD4TMvcData();

        mvcData.setControllerName("Page");
        mvcData.setControllerAreaName("Core");
        mvcData.setActionName("Page");

        final PageTemplate pageTemplate = genericPage.getPageTemplate();
        final Map<String, Field> templateMeta = pageTemplate.getMetadata();
        String viewName = getStringValue(templateMeta, "view");
        if (Strings.isNullOrEmpty(viewName)) {
            viewName = pageTemplate.getTitle().replaceAll(" ", "");
        }

        final String[] parts = viewName.split(":");
        if (parts.length > 1) {
            mvcData.setViewName(parts[1]);
            mvcData.setAreaName(parts[0]);
        } else {
            mvcData.setViewName(viewName);
            mvcData.setAreaName("Core");
        }

        return mvcData;
    }

    private MvcData createRegionMvcData(ComponentPresentation cp) {
        final DD4TMvcData mvcData = new DD4TMvcData();

        mvcData.setControllerName("Region");
        mvcData.setControllerAreaName("Core");
        mvcData.setActionName("Region");

        final ComponentTemplate componentTemplate = cp.getComponentTemplate();
        final Map<String, Field> templateMeta = componentTemplate.getMetadata();
        String viewName = getStringValue(templateMeta, "regionView");
        if (Strings.isNullOrEmpty(viewName)) {
            final Matcher matcher = VIEW_NAME_PATTERN.matcher(componentTemplate.getTitle());
            viewName = matcher.matches() ? matcher.group(1) : "Main";
        }

        final String[] parts = viewName.split(":");
        if (parts.length > 1) {
            mvcData.setViewName(parts[1]);
            mvcData.setAreaName(parts[0]);
        } else {
            mvcData.setViewName(viewName);
            mvcData.setAreaName("Core");
        }

        return mvcData;
    }

    private MvcData createEntityMvcData(ComponentPresentation cp) {
        final DD4TMvcData mvcData = new DD4TMvcData();

        final ComponentTemplate componentTemplate = cp.getComponentTemplate();
        final Map<String, Field> templateMeta = componentTemplate.getMetadata();
        String controllerName = getStringValue(templateMeta, "controller");
        if (Strings.isNullOrEmpty(controllerName)) {
            controllerName = "Entity";
        }

        String[] parts = controllerName.split(":");
        if (parts.length > 1) {
            mvcData.setControllerName(parts[1]);
            mvcData.setControllerAreaName(parts[0]);
        } else {
            mvcData.setControllerName(controllerName);
            mvcData.setControllerAreaName("Core");
        }

        String actionName = getStringValue(templateMeta, "action");
        if (Strings.isNullOrEmpty(actionName)) {
            actionName = "Entity";
        }

        mvcData.setActionName(actionName);

        String viewName = getStringValue(templateMeta, "view");
        if (Strings.isNullOrEmpty(viewName)) {
            viewName = componentTemplate.getTitle().replaceAll("\\[.*\\]|\\s", "");
        }

        parts = viewName.split(":");
        if (parts.length > 1) {
            mvcData.setViewName(parts[1]);
            mvcData.setAreaName(parts[0]);
        } else {
            mvcData.setViewName(viewName);
            mvcData.setAreaName("Core");
        }

        String regionView = getStringValue(templateMeta, "regionView");
        if (Strings.isNullOrEmpty(regionView)) {
            regionView = "Main";
        }

        parts = regionView.split(":");
        if (parts.length > 1) {
            mvcData.setRegionName(parts[1]);
            mvcData.setRegionAreaName(parts[0]);
        } else {
            mvcData.setRegionName(regionView);
            mvcData.setRegionAreaName("Core");
        }

        final Map<String, String> routeValues = new HashMap<>();
        for (String routeValue : Strings.nullToEmpty(getStringValue(templateMeta, "routeValues")).split(",")) {
            parts = routeValue.split(":");
            if (parts.length > 1 && !routeValues.containsKey(parts[0])) {
                routeValues.put(parts[0], parts[1]);
            }
        }

        mvcData.setRouteValues(routeValues);

        return mvcData;
    }
}

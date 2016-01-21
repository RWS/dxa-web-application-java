package com.sdl.webapp.tridion.mapping;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.RegionBuilder;
import com.sdl.webapp.common.api.content.RegionBuilderCallback;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.FieldConverterRegistry;
import com.sdl.webapp.util.dd4t.FieldUtils;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.resolvers.LinkResolver;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public final class PageBuilderImpl implements PageBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(EntityBuilderImpl.class);

    private static final String IMAGE_FIELD_NAME = "image";
    private static final String REGION_FOR_PAGE_TITLE_COMPONENT = "Main";
    private static final String STANDARD_METADATA_FIELD_NAME = "standardMeta";
    private static final String STANDARD_METADATA_TITLE_FIELD_NAME = "name";
    private static final String STANDARD_METADATA_DESCRIPTION_FIELD_NAME = "description";
    private static final String COMPONENT_PAGE_TITLE_FIELD_NAME = "headline";
    private static final String REGIONS_METADATA_FIELD_NAME = "regions";
    private static final String REGIONS_METADATA_FIELD_NAME_VIEW = "view";
    private static final String REGIONS_METADATA_FIELD_NAME_NAME = "name";
    private static final String DEFAULT_AREA_NAME = "Core";
    private static final String PAGE_CONTROLLER_NAME = "Page";
    private static final String PAGE_ACTION_NAME = "Page";
    private static final String REGION_CONTROLLER_NAME = "Region";
    private static final String REGION_ACTION_NAME = "Region";
    private static final String DEFAULT_REGION_NAME = "Main";
    private static final Pattern REGION_VIEW_NAME_PATTERN = Pattern.compile(".*\\[(.*)\\]");

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    private LinkResolver linkResolver;

    @Autowired
    private ConditionalEntityEvaluator conditionalEntityEvaluator;

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private FieldConverterRegistry fieldConverterRegistry;

    @Autowired
    private RegionBuilder regionBuilder;

    @Autowired
    private SemanticMapper semanticMapper;

    @Autowired
    private ComponentPresentationFactory dd4tComponentPresentationFactory;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private RegionModel getRegionFromIncludePage(PageModel page, String includeFileName) {
        try {
            String regionName = page.getName().replace(" ", "-");
            //if a include page title contains an area name, remove it from the region name, as this name should not be qualified
            if (regionName.contains(":")) {
                regionName = regionName.substring(regionName.indexOf(":") + 1);
            }

            MvcData regionMvcData = MvcDataCreator.creator()
                    .fromQualifiedName(regionName)
                    .defaults(DefaultsMvcData.CORE_REGION)
                    .create();

            RegionModelImpl region = new RegionModelImpl(regionName);
            region.setName(regionName);
            region.setMvcData(regionMvcData);
            ImmutableMap.Builder<String, String> xpmMetaDataBuilder = ImmutableMap.builder();

            xpmMetaDataBuilder.put(RegionModelImpl.INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY, page.getId());
            xpmMetaDataBuilder.put(RegionModelImpl.INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY, page.getTitle());
            xpmMetaDataBuilder.put(RegionModelImpl.INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY, includeFileName);

            region.setRegions(new RegionModelSetImpl());
            region.setXpmMetadata(xpmMetaDataBuilder.build());
            return region;

        } catch (DxaException e) {
            LOG.error("Error creating new MvcData from includepage", e);
            return null;
        }
    }

    @Override
    public PageModel createPage(org.dd4t.contentmodel.Page genericPage, PageModel originalPageModel, Localization localization, ContentProvider contentProvider)
            throws ContentProviderException {

        final PageModel page;
        try {
            page = createPageModel(genericPage, localization);
        } catch (DxaException e) {
            throw new ContentProviderException(e);
        }


        String localizationPath = localization.getPath();
        if (!localizationPath.endsWith("/")) {
            localizationPath = localizationPath + "/";
        }

        final RegionModelSet regionMap = this.createPredefinedRegions(genericPage.getPageTemplate());

        final RegionModelSet cpRegions = this.regionBuilder.buildRegions(page, this.conditionalEntityEvaluator,
                genericPage.getComponentPresentations(), new DD4TRegionBuilderCallback(),
                localization, this.viewModelRegistry);
        if (cpRegions != null) {
            for (RegionModel model : cpRegions) {
                if (!regionMap.containsName(model.getName())) {
                    regionMap.add(model);
                } else {
                    if (!regionMap.get(model.getName()).getMvcData().equals(model.getMvcData())) {
                        LOG.warn("Region '%s' is defined with conflicting MVC data: [%s] and [%s]. Using the former.", model.getName(), regionMap.get(model.getName()).getMvcData(), model.getMvcData());
                        for (EntityModel e : model.getEntities()) {
                            regionMap.get(model.getName()).addEntity(e);
                        }
                    }
                }
            }
        }
        // Get and add includes
        final String pageTypeId = genericPage.getPageTemplate().getId().split("-")[1];
        for (String include : localization.getIncludes(pageTypeId)) {
            final String includeUrl = localizationPath + include;
            PageModel includePageModel = contentProvider.getPageModel(includeUrl, localization);
            final RegionModel includePageRegion = getRegionFromIncludePage(includePageModel, include);

            RegionModel existingRegion;
            if (regionMap.containsName(includePageRegion.getName())) {
                // Region with same name already exists; merge include Page Region.
                existingRegion = regionMap.get(includePageRegion.getName());

                existingRegion.getRegions().addAll(includePageModel.getRegions());

                if (existingRegion.getXpmMetadata() != null) {
                    existingRegion.getXpmMetadata().remove(RegionModelImpl.INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY);
                    existingRegion.getXpmMetadata().remove(RegionModelImpl.INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY);
                    existingRegion.getXpmMetadata().remove(RegionModelImpl.INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY);
                }
                LOG.info("Merged Include Page [%s] into Region [%s]. Note that merged Regions can't be edited properly in XPM (yet).",
                        includePageModel, existingRegion);
            } else {
                includePageRegion.getRegions().addAll(includePageModel.getRegions());
                regionMap.add(includePageRegion);
            }
        }

        page.setRegions(regionMap);

        return page;
    }

    private PageModel createPageModel(org.dd4t.contentmodel.Page genericPage, Localization localization) throws DxaException, ContentProviderException {
        MvcData pageMvcData = createPageMvcData(genericPage.getPageTemplate());
        Class pageModelType = viewModelRegistry.getViewModelType(pageMvcData);

        Schema pageMetadataSchema = genericPage.getSchema();

        PageModel pageModel;
        if (pageModelType == PageModelImpl.class) {
            // Standard Page Model
            pageModel = new PageModelImpl();
        } else if (pageMetadataSchema == null) {
            // Custom Page Model but no Page metadata that can be mapped; simply create a Page Model instance of the right type.
            try {
                pageModel = (PageModel) pageModelType.newInstance();
            } catch (InstantiationException e) {
                throw new DxaException(String.format("Error instantiating new page of type %s", pageModelType), e);
            } catch (IllegalAccessException e) {
                throw new DxaException(String.format("Illegal access exception when instantiating new page of type %s", pageModelType), e);
            }
        } else {
            // Custom Page Model and Page metadata is present; do full-blown model mapping.
            String[] schemaTcmUriParts = pageMetadataSchema.getId().split("-");

            final SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(schemaTcmUriParts[1]));

            final Class<? extends ViewModel> entityClass = viewModelRegistry.getMappedModelTypes(semanticSchema.getFullyQualifiedNames());
            pageModel = (PageModel) createViewModel(entityClass, semanticSchema, genericPage);
        }

        pageModel.setId(genericPage.getId());

        // It's confusing, but what DD4T calls the "title" is what is called the "name" in the view model
        pageModel.setName(genericPage.getTitle());

        final Map<String, String> pageMeta = new HashMap<>();
        final String title = processPageMetadata(genericPage, pageMeta, localization);
        pageModel.setTitle(title);
        pageModel.setMeta(pageMeta);
        pageModel.setMvcData(pageMvcData);

        pageModel.setXpmMetadata(createXpmMetaData(genericPage, localization));
        pageModel.setMvcData(createPageMvcData(genericPage.getPageTemplate()));

        String htmlClasses = FieldUtils.getStringValue(genericPage.getPageTemplate().getMetadata(), "htmlClasses");
        if (!Strings.isNullOrEmpty(htmlClasses)) {
            pageModel.setHtmlClasses(htmlClasses.replaceAll("[^\\w\\- ]", ""));
        }

        return pageModel;
    }

    private ViewModel createViewModel(Class<? extends ViewModel> entityClass, SemanticSchema semanticSchema, Page page) throws ContentProviderException {

        final ViewModel entity;
        try {
            entity = semanticMapper.createEntity(entityClass, semanticSchema.getSemanticFields(),
                    new SemanticFieldDataProviderImpl(
                            new SemanticFieldDataProviderImpl.PageEntity(page), fieldConverterRegistry, this.modelBuilderPipeline));
        } catch (SemanticMappingException e) {
            throw new ContentProviderException(e);
        }
        return entity;
    }

    private RegionModelSet createPredefinedRegions(PageTemplate pageTemplate) {

        final Map<String, Field> pageTemplateMeta = pageTemplate.getMetadata();

        RegionModelSet regions = new RegionModelSetImpl();

        if (pageTemplateMeta == null || !pageTemplateMeta.containsKey(REGIONS_METADATA_FIELD_NAME))// TODO: "region" instead of "regions"
        {
            LOG.debug("No Region metadata defined for Page Template '{}'.", pageTemplate.getId());
            return regions;
        }

        BaseField regionsMetaField = (BaseField) pageTemplateMeta.get(REGIONS_METADATA_FIELD_NAME);
        if (regionsMetaField != null && !regionsMetaField.getEmbeddedValues().isEmpty()) {
            for (FieldSet regionField : regionsMetaField.getEmbeddedValues()) {
                Map<String, Field> region = regionField.getContent();
                if (!region.containsKey(REGIONS_METADATA_FIELD_NAME_VIEW)) {
                    LOG.warn("Region metadata without 'view' field encountered in metadata of Page Template '{}'.", pageTemplate.getId());
                    continue;
                }
                String view = FieldUtils.getStringValue(region, REGIONS_METADATA_FIELD_NAME_VIEW);

                String name = view;
                if (region.containsKey(REGIONS_METADATA_FIELD_NAME_NAME)) {
                    name = FieldUtils.getStringValue(region, REGIONS_METADATA_FIELD_NAME_NAME);
                    if (Strings.isNullOrEmpty(name)) {
                        name = view;
                    }
                }

                MvcData regionMvcData = MvcDataCreator.creator()
                        .fromQualifiedName(view)
                        .defaults(DefaultsMvcData.CORE_REGION)
                        .builder()
                        .regionName(name)
                        .build();

                try {
                    RegionModel regionModel = createRegionModel(regionMvcData);
                    regions.add(regionModel);
                } catch (IllegalAccessException | InstantiationException | DxaException | InvocationTargetException | NoSuchMethodException e) {
                    LOG.error("Error creating region for view '{}'.", view, e);
                }

            }
        }
        return regions;
    }

    private RegionModel createRegionModel(MvcData regionMvcData) throws IllegalAccessException, InstantiationException, DxaException, NoSuchMethodException, InvocationTargetException {
        Class regionModelType = this.viewModelRegistry.getViewModelType(regionMvcData);

        RegionModel regionModel = (RegionModel) regionModelType.getDeclaredConstructor(String.class).newInstance(regionMvcData.getViewName());
        regionModel.setMvcData(regionMvcData);
        return regionModel;
    }

    private String extract(Map<String, Field> metaMap, String key) {
        return metaMap.get(key).getValues().get(0).toString();
    }

    private String processPageMetadata(org.dd4t.contentmodel.Page page, Map<String, String> pageMeta, Localization localization) {
        // Process page metadata fields
        if (page.getMetadata() != null) {
            for (Field field : page.getMetadata().values()) {
                pageMeta.putAll(processMetadataField(field));
            }
        }

        String description = pageMeta.get("description");
        String title = pageMeta.get("title");
        String image = pageMeta.get(IMAGE_FIELD_NAME);

        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(description)) {
            for (ComponentPresentation cp : page.getComponentPresentations()) {
                if (Objects.equals(REGION_FOR_PAGE_TITLE_COMPONENT, getRegionName(cp))) {
                    final org.dd4t.contentmodel.Component component = cp.getComponent();

                    final Map<String, Field> metadata = component.getMetadata();
                    BaseField standardMetaField = (BaseField) metadata.get(STANDARD_METADATA_FIELD_NAME);
                    if (standardMetaField != null && !standardMetaField.getEmbeddedValues().isEmpty()) {
                        final Map<String, Field> standardMeta = standardMetaField.getEmbeddedValues().get(0).getContent();
                        if (Strings.isNullOrEmpty(title) && standardMeta.containsKey(STANDARD_METADATA_TITLE_FIELD_NAME)) {
                            title = extract(standardMeta, STANDARD_METADATA_TITLE_FIELD_NAME);
                        }
                        if (Strings.isNullOrEmpty(description) && standardMeta.containsKey(STANDARD_METADATA_DESCRIPTION_FIELD_NAME)) {
                            description = extract(standardMeta, STANDARD_METADATA_DESCRIPTION_FIELD_NAME);
                        }
                    }

                    final Map<String, Field> content = component.getContent();
                    if (Strings.isNullOrEmpty(title) && content.containsKey(COMPONENT_PAGE_TITLE_FIELD_NAME)) {
                        title = extract(content, COMPONENT_PAGE_TITLE_FIELD_NAME);
                    }

                    if (Strings.isNullOrEmpty(image) && content.containsKey(IMAGE_FIELD_NAME)) {
                        image = ((BaseField) content.get(IMAGE_FIELD_NAME))
                                .getLinkedComponentValues().get(0).getMultimedia().getUrl();
                    }
                    break;
                }
            }
        }

        // Use page title if no title found
        if (Strings.isNullOrEmpty(title)) {
            title = page.getTitle();
            if (title.equalsIgnoreCase("index") || title.equalsIgnoreCase("default")) {
                // Use default page title from configuration if nothing better was found
                title = localization.getResource("core.defaultPageTitle");
            }
        }

        title = title.replaceFirst("\\d{3}\\s", "");

        pageMeta.put("twitter:card", "summary");
        pageMeta.put("og:title", title);
        pageMeta.put("og:url", webRequestContext.getFullUrl());
        pageMeta.put("og:type", "article");
        pageMeta.put("og:locale", localization.getCulture());

        if (!Strings.isNullOrEmpty(description)) {
            pageMeta.put("og:description", description);
        }

        if (!Strings.isNullOrEmpty(image)) {
            pageMeta.put("og:image", webRequestContext.getBaseUrl() + webRequestContext.getContextPath() + image);
        }

        if (!pageMeta.containsKey("description")) {
            pageMeta.put("description", !Strings.isNullOrEmpty(description) ? description : title);
        }

        String titlePostfix = localization.getResource("core.pageTitleSeparator") + localization.getResource("core.pageTitlePostfix");

        return title + titlePostfix;
    }

    private Map<String, String> processMetadataField(final Field field) {
        Map<String, String> result = new HashMap<>();

        // If it's an embedded field, then process the subfields
        if (field.getFieldType() == FieldType.EMBEDDED) {
            final List<FieldSet> embeddedValues = ((BaseField) field).getEmbeddedValues();
            if (embeddedValues != null && !embeddedValues.isEmpty()) {
                for (Field subField : embeddedValues.get(0).getContent().values()) {
                    result.putAll(processMetadataField(subField));
                }
            }
        } else {
            final String fieldName = field.getName();

            String value;
            switch (fieldName) {
                case "internalLink":
                    final String componentId = ((BaseField) field).getTextValues().get(0);
                    try {
                        value = linkResolver.resolve(componentId);
                    } catch (SerializationException | ItemNotFoundException e) {
                        LOG.warn("Error while resolving link: {}", componentId);
                        value = componentId;
                    }
                    break;
                case IMAGE_FIELD_NAME:
                    value = ((BaseField) field).getLinkedComponentValues().get(0).getMultimedia().getUrl();
                    break;
                default:
                    value = Joiner.on(',').join(field.getValues());
                    break;
            }

            if (!(StringUtils.isEmpty(value) || result.containsKey(fieldName))) {
                result.put(fieldName, value);
            }
        }
        return result;
    }

    private String getRegionName(ComponentPresentation cp) {
        final Map<String, Field> templateMeta = cp.getComponentTemplate().getMetadata();
        if (templateMeta != null) {
            String regionName = FieldUtils.getStringValue(templateMeta, "regionName");
            if (Strings.isNullOrEmpty(regionName)) {
                //fallback if region name field is empty, use regionView name
                regionName = FieldUtils.getStringValue(templateMeta, "regionView");
                if (Strings.isNullOrEmpty(regionName)) {
                    regionName = DEFAULT_REGION_NAME;
                }
            }
            return regionName;
        }

        return null;
    }

    private Map<String, String> createXpmMetaData(org.dd4t.contentmodel.Page page, Localization localization) {
        final PageTemplate pageTemplate = page.getPageTemplate();

        ImmutableMap.Builder<String, String> xpmMetaDataBuilder = ImmutableMap.builder();
        xpmMetaDataBuilder.put("PageID", page.getId());
        xpmMetaDataBuilder.put("PageModified", ISODateTimeFormat.dateHourMinuteSecond().print(page.getRevisionDate()));
        xpmMetaDataBuilder.put("PageTemplateID", pageTemplate.getId());
        xpmMetaDataBuilder.put("PageTemplateModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(pageTemplate.getRevisionDate()));

        xpmMetaDataBuilder.put("CmsUrl", localization.getConfiguration("core.cmsurl"));

        return xpmMetaDataBuilder.build();
    }

    private MvcData createPageMvcData(PageTemplate pageTemplate) {
        final String[] viewNameParts = getPageViewNameParts(pageTemplate);
        return MvcDataCreator.creator()
                .defaults(DefaultsMvcData.CORE_PAGE)
                .builder()
                .areaName(viewNameParts[0])
                .viewName(viewNameParts[1])
                .metadata(getMvcMetadata(pageTemplate))
                .build();
    }

    private MvcData createRegionMvcData(ComponentTemplate componentTemplate) {
        final String[] viewNameParts = getRegionViewNameParts(componentTemplate);
        return MvcDataCreator.creator()
                .defaults(DefaultsMvcData.CORE_REGION)
                .builder()
                .areaName(viewNameParts[0])
                .viewName(viewNameParts[1])
                .build();
    }

    private String[] getPageViewNameParts(PageTemplate pageTemplate) {
        String fullName = FieldUtils.getStringValue(pageTemplate.getMetadata(), "view");
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = pageTemplate.getTitle().replaceAll(" ", "");
        }
        return splitName(fullName);
    }

    private String[] getRegionViewNameParts(ComponentTemplate componentTemplate) {
        String fullName = FieldUtils.getStringValue(componentTemplate.getMetadata(), "regionView");
        if (Strings.isNullOrEmpty(fullName)) {
            final Matcher matcher = REGION_VIEW_NAME_PATTERN.matcher(componentTemplate.getTitle());
            fullName = matcher.matches() ? matcher.group(1) : DEFAULT_REGION_NAME;
        }
        return splitName(fullName);
    }

    private String[] splitName(String name) {
        final String[] parts = name.split(":");
        return parts.length > 1 ? parts : new String[]{DEFAULT_AREA_NAME, name};
    }

    private Map<String, Object> getMvcMetadata(PageTemplate pageTemplate) {

        Map<String, Object> metadata = new HashMap<>();
        Map<String, Field> metadataFields = pageTemplate.getMetadata();
        for (Map.Entry<String, Field> entry : metadataFields.entrySet()) {

            String fieldName = entry.getKey();
            if (fieldName.equals("view") ||
                    fieldName.equals("includes")) {
                continue;
            }
            Field field = entry.getValue();
            if (field.getFieldType() == FieldType.EMBEDDED) {
                // Output embedded field as List<Map<String,String>>
                //
                List<Map<String, String>> embeddedDataList = new ArrayList<>();
                for (Object value : field.getValues()) {
                    FieldSet fieldSet = (FieldSet) value;
                    Map<String, String> embeddedData = new HashMap<>();
                    for (String subFieldName : fieldSet.getContent().keySet()) {
                        Field subField = fieldSet.getContent().get(subFieldName);
                        if (subField.getValues().size() > 0) {
                            embeddedData.put(subFieldName, subField.getValues().get(0).toString());
                        }
                    }
                    embeddedDataList.add(embeddedData);
                }
                metadata.put(fieldName, embeddedDataList);
            } else {
                // Output other field types as single-value text fields
                //
                if (field.getValues().size() > 0) {
                    metadata.put(fieldName, field.getValues().get(0).toString()); // Assume single-value text fields for template metadata
                }
            }
        }
        return metadata;
    }

    protected class DD4TRegionBuilderCallback implements RegionBuilderCallback {
        @Override
        public EntityModel buildEntity(Object source, Localization localization) throws ContentProviderException {

            ComponentPresentation componentPresentation = (ComponentPresentation) source;
            if (componentPresentation.isDynamic()) {
                try {

                    // Fetch the dynamic component presentation and replace the dummy static one
                    componentPresentation = dd4tComponentPresentationFactory.getComponentPresentation(componentPresentation.getComponent().getId(), componentPresentation.getComponentTemplate().getId());
                } catch (Exception e) {
                    throw new ContentProviderException("Could not fetch dynamic component presentation.", e);
                }
            }
            return modelBuilderPipeline.createEntityModel(componentPresentation, localization);
        }

        @Override
        public String getRegionName(Object source) throws ContentProviderException {
            return PageBuilderImpl.this.getRegionName((ComponentPresentation) source);
        }

        @Override
        public MvcData getRegionMvcData(Object source) throws ContentProviderException {
            return createRegionMvcData(((ComponentPresentation) source).getComponentTemplate());
        }
    }
}

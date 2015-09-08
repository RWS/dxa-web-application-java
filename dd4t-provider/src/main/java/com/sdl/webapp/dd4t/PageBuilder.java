package com.sdl.webapp.dd4t;

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
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;

import org.dd4t.contentmodel.*;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.resolvers.LinkResolver;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sdl.webapp.dd4t.fieldconverters.FieldUtils.getStringValue;

@Component
final class PageBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(PageBuilder.class);

    private static final String REGION_FOR_PAGE_TITLE_COMPONENT = "Main";
    private static final String STANDARD_METADATA_FIELD_NAME = "standardMeta";
    private static final String STANDARD_METADATA_TITLE_FIELD_NAME = "name";
    private static final String STANDARD_METADATA_DESCRIPTION_FIELD_NAME = "description";
    private static final String COMPONENT_PAGE_TITLE_FIELD_NAME = "headline";

    private static final String DEFAULT_AREA_NAME = "Core";

    private static final String PAGE_CONTROLLER_NAME = "Page";
    private static final String PAGE_ACTION_NAME = "Page";

    private static final String REGION_CONTROLLER_NAME = "Region";
    private static final String REGION_ACTION_NAME = "Region";

    private static final String DEFAULT_REGION_NAME = "Main";

    private static final Pattern REGION_VIEW_NAME_PATTERN = Pattern.compile(".*\\[(.*)\\]");

    private final EntityBuilder entityBuilder;

    private final LinkResolver linkResolver;

    private final ConditionalEntityEvaluator conditionalEntityEvaluator;
    
    private final WebRequestContext webRequestContext;

    private final RegionBuilder regionBuilder;

    private final ComponentPresentationFactory dd4tComponentPresentationFactory;

    class DD4TRegionBuilderCallback implements RegionBuilderCallback {
        @Override
        public EntityModel buildEntity(Object source, Localization localization) throws ContentProviderException {

            ComponentPresentation componentPresentation = (ComponentPresentation) source;
            if ( componentPresentation.isDynamic() ) {
                try {

                    // Fetch the dynamic component presentation and replace the dummy static one
                    //
                    componentPresentation = dd4tComponentPresentationFactory.getComponentPresentation(componentPresentation.getComponent().getId(), componentPresentation.getComponentTemplate().getId());
                }
                catch ( Exception e ) {
                    throw new ContentProviderException("Could not fetch dynamic component presentation.", e);
                }
            }
            return entityBuilder.createEntity(componentPresentation, localization);
        }

        @Override
        public String getRegionName(Object source) throws ContentProviderException {
            return PageBuilder.this.getRegionName((ComponentPresentation) source);
        }

        @Override
        public MvcData getRegionMvcData(Object source) throws ContentProviderException {
            return createRegionMvcData(((ComponentPresentation) source).getComponentTemplate());
        }
    }

    @Autowired
    PageBuilder(EntityBuilder entityBuilder,
                RegionBuilder regionBuilder,
                LinkResolver linkResolver,
                ConditionalEntityEvaluator conditionalEntityEvaluator,
                ComponentPresentationFactory dd4tComponentPresentationFactory,
                WebRequestContext webRequestContext) {
        this.entityBuilder = entityBuilder;
        this.regionBuilder = regionBuilder;
        this.linkResolver = linkResolver;
        this.conditionalEntityEvaluator = conditionalEntityEvaluator;
        this.dd4tComponentPresentationFactory = dd4tComponentPresentationFactory;
        this.webRequestContext = webRequestContext;
    }
    
    private RegionModel getRegionFromIncludePage(PageModel page, String includeFileName)
    {
    	try {
			String regionName = page.getName().replace(" ", "-");
    		MvcDataImpl regionMvcData = new MvcDataImpl(regionName);
			regionMvcData = InitializeRegionMvcData(regionMvcData);
			RegionModelImpl region = new RegionModelImpl();
			region.setName(regionName);
			region.setMvcData(regionMvcData);
			ImmutableMap.Builder<String, String> xpmMetaDataBuilder = ImmutableMap.builder();
  		    
			xpmMetaDataBuilder.put(RegionModelImpl.IncludedFromPageIdXpmMetadataKey, page.getId());
			xpmMetaDataBuilder.put(RegionModelImpl.IncludedFromPageTitleXpmMetadataKey, page.getTitle());
			xpmMetaDataBuilder.put(RegionModelImpl.IncludedFromPageFileNameXpmMetadataKey, includeFileName);
		        
			region.setRegions(new RegionModelSetImpl());
			region.setXpmMetadata(xpmMetaDataBuilder.build());
			return region;
			
		} catch (DxaException e) {
			LOG.error("Error creating new MvcData from includepage", e);
			return null;
		}
    }

    private MvcDataImpl InitializeRegionMvcData(MvcDataImpl regionMvcData) {
    	 if (Strings.isNullOrEmpty(regionMvcData.getControllerName()))
         {
             regionMvcData.setControllerName(REGION_CONTROLLER_NAME);
             regionMvcData.setControllerAreaName(DEFAULT_AREA_NAME);
         }
         else if (Strings.isNullOrEmpty(regionMvcData.getControllerAreaName()))
         {
             regionMvcData.setControllerAreaName(regionMvcData.getAreaName());
         }
         regionMvcData.setActionName(REGION_ACTION_NAME);
         
         return regionMvcData;
	}
    
	PageModel createPage(org.dd4t.contentmodel.Page genericPage, Localization localization, ContentProvider contentProvider)
            throws ContentProviderException {
        final PageModelImpl page = new PageModelImpl();

        page.setId(genericPage.getId());

        // It's confusing, but what DD4T calls the "title" is what is called the "name" in the view model
        page.setName(genericPage.getTitle());

        final Map<String, String> pageMeta = new HashMap<>();
        final String title = processPageMetadata(genericPage, pageMeta, localization);
        page.setTitle(title);
        page.setMeta(pageMeta);

        String localizationPath = localization.getPath();
        if (!localizationPath.endsWith("/")) {
            localizationPath = localizationPath + "/";
        }

        // = new RegionModelSetImpl();
        final RegionModelSet regionMap = this.regionBuilder.buildRegions(page, this.conditionalEntityEvaluator, genericPage.getComponentPresentations(), new DD4TRegionBuilderCallback(), localization);
   
        
        // Get and add includes
        final String pageTypeId = genericPage.getPageTemplate().getId().split("-")[1];
        for (String include : localization.getIncludes(pageTypeId)) {
            final String includeUrl = localizationPath + include;
            PageModel includePageModel = contentProvider.getPageModel(includeUrl, localization);
            final RegionModel includePageRegion = getRegionFromIncludePage(includePageModel, include);
            
            RegionModel existingRegion;
            if(regionMap.containsKey(includePageRegion.getName()))
            {
            	// Region with same name already exists; merge include Page Region.
            	existingRegion = regionMap.get(includePageRegion.getName());
            	
            	existingRegion.getRegions().addAll(includePageModel.getRegions());
            	
            	if (existingRegion.getXpmMetadata() != null)
                {
                    existingRegion.getXpmMetadata().remove(RegionModelImpl.IncludedFromPageIdXpmMetadataKey);
                    existingRegion.getXpmMetadata().remove(RegionModelImpl.IncludedFromPageTitleXpmMetadataKey);
                    existingRegion.getXpmMetadata().remove(RegionModelImpl.IncludedFromPageFileNameXpmMetadataKey);
                }
            	 LOG.info("Merged Include Page [%s] into Region [%s]. Note that merged Regions can't be edited properly in XPM (yet).",
                         includePageModel, existingRegion);
            }
            else
            {
            	  includePageRegion.getRegions().addAll(includePageModel.getRegions());
            	  regionMap.add(includePageRegion);
            }
        }

        page.setXpmMetadata(createXpmMetaData(genericPage, localization));
        page.setMvcData(createPageMvcData(genericPage.getPageTemplate()));

        String htmlClasses = getStringValue(genericPage.getPageTemplate().getMetadata(), "htmlClasses");
        if (Strings.isNullOrEmpty(htmlClasses)) {
            page.setHtmlClasses(htmlClasses.replaceAll("[^\\w\\-\\ ]", ""));
        }
          
        //regionMap.addAll(regions.values());
        page.setRegions(regionMap);

        return page;
    }

    private String processPageMetadata(org.dd4t.contentmodel.Page page, Map<String, String> pageMeta, Localization localization) {
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
                    final org.dd4t.contentmodel.Component component = cp.getComponent();

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
                        image = ((BaseField) content.get("image")).getLinkedComponentValues().get(0)
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

    private void processMetadataField(Field field, Map<String, String> pageMeta) {
        // If it's an embedded field, then process the subfields
        if (field.getFieldType() == FieldType.EMBEDDED) {
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
                    final String componentId = ((BaseField) field).getTextValues().get(0);
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
        final MvcDataImpl mvcData = new MvcDataImpl();

        mvcData.setControllerAreaName(DEFAULT_AREA_NAME);
        mvcData.setControllerName(PAGE_CONTROLLER_NAME);
        mvcData.setActionName(PAGE_ACTION_NAME);

        final String[] viewNameParts = getPageViewNameParts(pageTemplate);
        mvcData.setAreaName(viewNameParts[0]);
        mvcData.setViewName(viewNameParts[1]);

        mvcData.setMetadata(this.getMvcMetadata(pageTemplate));

        return mvcData;
    }

    private MvcData createRegionMvcData(ComponentTemplate componentTemplate) {
        final MvcDataImpl mvcData = new MvcDataImpl();

        mvcData.setControllerAreaName(DEFAULT_AREA_NAME);
        mvcData.setControllerName(REGION_CONTROLLER_NAME);
        mvcData.setActionName(REGION_ACTION_NAME);

        final String[] viewNameParts = getRegionViewNameParts(componentTemplate);
        mvcData.setAreaName(viewNameParts[0]);
        mvcData.setViewName(viewNameParts[1]);

        return mvcData;
    }

    private String[] getPageViewNameParts(PageTemplate pageTemplate) {
        String fullName = getStringValue(pageTemplate.getMetadata(), "view");
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = pageTemplate.getTitle().replaceAll(" ", "");
        }
        return splitName(fullName);
    }

    private String[] getRegionViewNameParts(ComponentTemplate componentTemplate) {
        String fullName = getStringValue(componentTemplate.getMetadata(), "regionView");
        if (Strings.isNullOrEmpty(fullName)) {
            final Matcher matcher = REGION_VIEW_NAME_PATTERN.matcher(componentTemplate.getTitle());
            fullName = matcher.matches() ? matcher.group(1) : DEFAULT_REGION_NAME;
        }
        return splitName(fullName);
    }

    private String[] splitName(String name) {
        final String[] parts = name.split(":");
        return parts.length > 1 ? parts : new String[] { DEFAULT_AREA_NAME, name };
    }

    private Map<String,Object> getMvcMetadata(PageTemplate pageTemplate) {

        Map<String,Object> metadata = new HashMap<>();
        Map<String,Field> metadataFields = pageTemplate.getMetadata();
        for ( String fieldName : metadataFields.keySet() ) {
            if ( fieldName.equals("view") ||
                 fieldName.equals("includes")) {
                continue;
            }
            Field field = metadataFields.get(fieldName);
            if ( field.getFieldType() == FieldType.EMBEDDED ) {
                // Output embedded field as List<Map<String,String>>
                //
                List<Map<String,String>> embeddedDataList = new ArrayList<>();
                for ( Object value : field.getValues() ) {
                    FieldSet fieldSet = (FieldSet) value;
                    Map<String,String> embeddedData = new HashMap<>();
                    for ( String subFieldName : fieldSet.getContent().keySet() ) {
                        Field subField = fieldSet.getContent().get(subFieldName);
                        if ( subField.getValues().size() > 0 ) {
                            embeddedData.put(subFieldName, subField.getValues().get(0).toString());
                        }
                    }
                    embeddedDataList.add(embeddedData);
                }
                metadata.put(fieldName, embeddedDataList);
            }
            else {
                // Output other field types as single-value text fields
                //
                if (field.getValues().size() > 0) {
                    metadata.put(fieldName, field.getValues().get(0).toString()); // Assume single-value text fields for template metadata
                }
            }
        }
        return metadata;
    }
}

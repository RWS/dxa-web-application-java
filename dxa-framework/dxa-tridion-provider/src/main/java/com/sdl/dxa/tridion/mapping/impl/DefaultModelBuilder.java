package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.BinaryContentData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.ExternalContentData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.dxa.api.datamodel.model.util.ListWrapper;
import com.sdl.dxa.caching.ConditionalKey;
import com.sdl.dxa.caching.ConditionalKey.ConditionalKeyBuilder;
import com.sdl.dxa.caching.LocalizationAwareCacheKey;
import com.sdl.dxa.caching.NeverCached;
import com.sdl.dxa.caching.wrapper.EntitiesCache;
import com.sdl.dxa.caching.wrapper.PagesCopyingCache;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.entity.ExceptionEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;
import static com.sdl.webapp.common.util.StringUtils.dashify;

/**
 * Default implementation of {@link EntityModelBuilder} and {@link PageModelBuilder}. Priority of this builder is always {@code highest precedence}.
 *
 * @dxa.publicApi
 */
@Slf4j
@Service
public class DefaultModelBuilder implements EntityModelBuilder, PageModelBuilder {

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private SemanticMapper semanticMapper;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private PagesCopyingCache pagesCopyingCache;

    @Autowired
    private EntitiesCache entitiesCache;

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @Override
    public <T extends EntityModel> T buildEntityModel(@Nullable T originalEntityModel,
                                                      @NotNull EntityModelData modelData,
                                                      @Nullable Class<T> expectedClass) throws DxaException {
        try {
            MvcData mvcData = null;
            Class<? extends ViewModel> modelType;
            if (expectedClass != null) {
                log.debug("Expected class is pre-set to {} for model {}", expectedClass, modelData);
                // https://jira.sdl.com/browse/TSI-2273
                // we currently ignore the base type because of the issue but don't ignore the fact that it's set
                SemanticSchema semanticSchema = webRequestContext.getLocalization().getSemanticSchemas().get(Long.parseLong(modelData.getSchemaId()));
                modelType = viewModelRegistry.getMappedModelTypes(semanticSchema.getFullyQualifiedNames(), expectedClass);
            } else {
                mvcData = createMvcData(modelData.getMvcData(), DefaultsMvcData.ENTITY);
                log.debug("Expected class is not set explicitly, trying to get it from MvcData");
                modelType = viewModelRegistry.getViewModelType(mvcData);
            }
            if (modelType == null) throw new IllegalStateException("Could not determine ModelType " +
                    (expectedClass != null
                            ? " from pre-selected class " + expectedClass.getCanonicalName()
                            : " from MvcData class " + mvcData.getClass().getCanonicalName()));
            LocalizationAwareCacheKey key = entitiesCache.getSpecificKey(modelData,expectedClass);
            synchronized (this) {
                if (entitiesCache.containsKey(key)) {
                    //noinspection unchecked
                    return (T) entitiesCache.get(key);
                }
            }

            //noinspection unchecked
            T entityModel = (T) createViewModel(modelType, modelData);
            entityModel.setMvcData(mvcData);

            ((AbstractEntityModel) entityModel).setId(modelData.getId());
            fillViewModel(entityModel, modelData);

            processMediaItem(modelData, entityModel);
            synchronized (this) {
                entitiesCache.addAndGet(key, entityModel);
            }
            return entityModel;
        } catch (DxaException e) {
            throw new DxaException("Exception happened while creating a entity model from: " + modelData, e);
        }
    }

    @NotNull
    private MvcData createMvcData(MvcModelData modelData, DefaultsMvcData defaults) {
        return creator(MvcDataImpl.newBuilder()
                .actionName(modelData.getActionName())
                .areaName(modelData.getAreaName())
                .controllerAreaName(modelData.getControllerAreaName())
                .controllerName(modelData.getControllerName())
                .viewName(modelData.getViewName())
                .routeValues(modelData.getParameters())
        ).defaults(defaults).create();
    }

    @NotNull
    private <T extends ViewModel> T createViewModel(Class<T> viewModelType, @NonNull ViewModelData viewModelData) throws SemanticMappingException {
        Localization localization = webRequestContext.getLocalization();
        long schemaId = Long.parseLong(viewModelData.getSchemaId());
        SemanticSchema semanticSchema = localization.getSemanticSchemas().get(schemaId);

        List<SemanticSchema> allSchemas = semanticSchema == null ? getInheritedSemanticSchemas(viewModelData, localization) : Collections.emptyList();
        semanticSchema = semanticSchema == null && !allSchemas.isEmpty()
                ? allSchemas.get(0)
                : semanticSchema;
        Map<FieldSemantics, SemanticField> semanticFields = getAllSemanticFields(semanticSchema, viewModelData);
        DefaultSemanticFieldDataProvider dataProvider = DefaultSemanticFieldDataProvider.getFor(viewModelData, semanticSchema);
        return semanticMapper.createEntity(viewModelType, semanticFields, dataProvider);
    }

    protected List<SemanticSchema> getInheritedSemanticSchemas(ViewModelData viewModelData, Localization localization) {
        Object schemas = viewModelData.getExtensionData() != null ? viewModelData.getExtensionData().get("Schemas") : null;
        if (schemas == null ||
            !(schemas instanceof ListWrapper) ||
            ((ListWrapper) schemas).getValues().isEmpty()) {
            return Collections.emptyList();
        }
        ListWrapper<String> allInheritedSchemas = (ListWrapper<String>) schemas;
        return allInheritedSchemas.getValues()
                .stream()
                .map(schemaId -> localization.getSemanticSchemas().get(Long.parseLong(schemaId)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @NotNull
    private Map<FieldSemantics, SemanticField> getAllSemanticFields(@Nullable SemanticSchema semanticSchema,
                                                                    @NotNull ViewModelData modelData) {
        if (semanticSchema == null) {
            return Collections.emptyMap();
        }
        final Map<FieldSemantics, SemanticField> semanticFields = semanticSchema.getSemanticFields();

        if (modelData.getExtensionData() == null) {
            return semanticFields;
        }
        Object schemas = modelData.getExtensionData().get("Schemas");
        if (schemas == null ||
            !(schemas instanceof ListWrapper) ||
            ((ListWrapper) schemas).getValues().isEmpty()) {
            return semanticFields;
        }
        if (log.isDebugEnabled()) log.debug("Found additional semantic schemas {} used in the view model {}", schemas, modelData);

        Localization localization = webRequestContext.getLocalization();
        Map<FieldSemantics, SemanticField> allAncestorsSemanticFields = new HashMap<>(semanticFields);

        //noinspection unchecked
        ListWrapper<String> inheritedSchemas = (ListWrapper<String>) schemas;
        allAncestorsSemanticFields.putAll(inheritedSchemas.getValues()
                .stream()
                .map(schemaId -> localization.getSemanticSchemas().get(Long.parseLong(schemaId)))
                .filter(Objects::nonNull)
                .map(SemanticSchema::getSemanticFields)
                .flatMap(fieldMap -> fieldMap.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return allAncestorsSemanticFields;
    }

    private void fillViewModel(@NotNull ViewModel viewModel, @NotNull ViewModelData modelData) {
        if (modelData.getExtensionData() != null) {
            modelData.getExtensionData().forEach(viewModel::addExtensionData);
        }

        if (viewModel instanceof AbstractViewModel && modelData.getXpmMetadata() != null) {
            ((AbstractViewModel) viewModel).setXpmMetadata(modelData.getXpmMetadata());
        }

        viewModel.setHtmlClasses(modelData.getHtmlClasses());
    }

    private <T extends EntityModel> void processMediaItem(EntityModelData modelData, T entityModel) throws DxaException {
        if (!(entityModel instanceof MediaItem)) {
            return;
        }
        MediaItem mediaItem = (MediaItem) entityModel;
        BinaryContentData binaryContent = modelData.getBinaryContent();
        if (binaryContent == null) {
            throw new DxaException("Unable to create Media Item ('" + mediaItem.getClass() + "') " +
                    "because the Data Model '" + mediaItem.getId() + "') \"' does not contain Binary Content Data.");
        }
        mediaItem.setUrl(binaryContent.getUrl());
        mediaItem.setFileName(binaryContent.getFileName());
        mediaItem.setMimeType(binaryContent.getMimeType());
        mediaItem.setFileSize(binaryContent.getFileSize());

        if (mediaItem instanceof EclItem) {
            EclItem eclItem = (EclItem) mediaItem;

            ExternalContentData externalContent = modelData.getExternalContent();
            if (externalContent == null) {
                throw new DxaException("Unable to create ECL Item ('" + eclItem.getClass() + "') " +
                        "because the Data Model '" + eclItem.getId() + "') \"' does not contain External Content Data.");
            }

            eclItem.setDisplayTypeId(externalContent.getDisplayTypeId());
            eclItem.setTemplateFragment(externalContent.getTemplateFragment());
            eclItem.setExternalMetadata(externalContent.getMetadata());
            eclItem.setUri(externalContent.getId());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @Override
    public PageModel buildPageModel(@Nullable PageModel originalPageModel, @NotNull PageModelData modelData) throws SemanticMappingException {
        LocalizationAwareCacheKey cacheKey = pagesCopyingCache.getSpecificKey(modelData);
        synchronized (this) {
            if (pagesCopyingCache.containsKey(cacheKey)) {
                return pagesCopyingCache.get(cacheKey);
            }
        }

        ConditionalKeyBuilder keyBuilder = ConditionalKey.builder().key(cacheKey);
        PageModel pageModel = instantiatePageModel(originalPageModel, modelData);

        if (pageModel == null) {
            log.info("Page Model is null, for model data id = {}", modelData.getId());
            return null;
        }

        fillViewModel(pageModel, modelData);
        pageModel.setId(modelData.getId());
        pageModel.setMeta(modelData.getMeta());
        pageModel.setName(modelData.getTitle());
        pageModel.setTitle(getPageTitle(modelData));
        pageModel.setUrl(modelData.getUrlPath());
        processRegions(modelData.getRegions(), keyBuilder, pageModel.getRegions());
        if (isNeverCached(pageModel)) {
            keyBuilder.skipCaching(true);
        }
        ConditionalKey conditionalKey = keyBuilder.build();
        pageModel.setStaticModel(!conditionalKey.isSkipCaching());
        synchronized (this) {
            return pagesCopyingCache.addAndGet(conditionalKey, pageModel);
        }
    }

    private void processRegions(List<RegionModelData> regions,
                                ConditionalKeyBuilder keyBuilder,
                                RegionModelSet regionsToAdd) throws SemanticMappingException {
        if (regions == null) {
            return;
        }
        AtomicReference<SemanticMappingException> exception = new AtomicReference<>();
        regions.stream()
                .map(regionModelDataLoc -> {
                    try {
                        return createRegionModel(regionModelDataLoc, keyBuilder);
                    } catch (SemanticMappingException ex) {
                        if (exception.get() != null) exception.set(ex);
                        return null;
                    }
                })
                .forEach(regionsToAdd::add);
        if (exception.get() != null) throw exception.get();
    }

    @Nullable
    private PageModel instantiatePageModel(@Nullable PageModel originalPageModel, @NotNull PageModelData modelData) throws SemanticMappingException {
        MvcData mvcData = createMvcData(modelData.getMvcData(), DefaultsMvcData.PAGE);
        log.debug("MvcData '{}' for PageModel {}", mvcData, modelData);

        if (originalPageModel != null) {
            log.warn("Original page model expected to be null but it's '{}'", originalPageModel);
            return originalPageModel;
        }
        PageModel pageModel = null;
        try {
            Class<? extends ViewModel> viewModelType = viewModelRegistry.getViewModelType(mvcData);

            log.debug("Instantiating a PageModel without a SchemaID = null, modelData = {}, view model type = '{}'", modelData, viewModelType);
            if (modelData.getSchemaId() == null) { //schema ID is not set, can't do semantic mapping
                pageModel = viewModelType == null ? new DefaultPageModel() : (PageModel) viewModelType.newInstance();
            } else { // semantic mapping is possible, let's do it
                pageModel = (PageModel) createViewModel(viewModelType, modelData);
            }
            pageModel.setMvcData(mvcData);
        } catch (ReflectiveOperationException | DxaException e) {
            throw new SemanticMappingException("Exception happened while creating a page model " + modelData.getId(), e);
        }
        return pageModel;
    }

    private String getPageTitle(PageModelData modelData) {
        Localization localization = webRequestContext.getLocalization();
        String title = "defaultPageTitle".equals(modelData.getTitle()) ? localization.getResource("core.defaultPageTitle") : modelData.getTitle();
        String separator = localization.getResource("core.pageTitleSeparator");
        String postfix = localization.getResource("core.pageTitlePostfix");
        log.trace("Model page title '{}', pageTitleSeparator '{}', postfix '{}'", title, separator, postfix);
        return title + separator + postfix;
    }

    private RegionModel createRegionModel(RegionModelData regionModelData, ConditionalKeyBuilder keyBuilder) throws SemanticMappingException {
        MvcData mvcData = createMvcData(regionModelData.getMvcData(), DefaultsMvcData.REGION);
        log.debug("MvcData '{}' for RegionModel {}", mvcData, regionModelData);

        try {
            Class<? extends ViewModel> viewModelType = viewModelRegistry.getViewModelType(mvcData);
            if (viewModelType == null) {
                throw new SemanticMappingException("Cannot find a view model type for " + mvcData);
            }

            RegionModel regionModel = createRegionModel(regionModelData, viewModelType);

            String schemaId = regionModelData.getSchemaId();
            regionModel.setSchemaId(schemaId);

            if (schemaId != null && !schemaId.isEmpty()){
                Localization localization = webRequestContext.getLocalization();
                SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(schemaId));

                List<SemanticSchema> allSchemas = semanticSchema == null ? getInheritedSemanticSchemas(regionModelData, localization) : Collections.emptyList();
                semanticSchema = semanticSchema == null && !allSchemas.isEmpty()
                        ? allSchemas.get(0)
                        : semanticSchema;
                Map<FieldSemantics, SemanticField> semanticFields = getAllSemanticFields(semanticSchema, regionModelData);
                semanticMapper.mapSemanticFields(viewModelType,
                        semanticFields,
                        DefaultSemanticFieldDataProvider.getFor(regionModelData, semanticSchema),
                        regionModel);
            }

            fillViewModel(regionModel, regionModelData);
            regionModel.setMvcData(mvcData);
            ((AbstractViewModel) regionModel).setXpmMetadata(regionModel.getXpmMetadata());
            if (isNeverCached(regionModel)) {
                keyBuilder.skipCaching(true);
            }
            processRegions(regionModelData.getRegions(), keyBuilder, regionModel.getRegions());
            if (regionModelData.getEntities() != null) {
                regionModelData.getEntities().stream()
                        .map(entityModelData -> {
                            EntityModel entityModel = createEntityModel(entityModelData, keyBuilder);
                            entityModel.setMvcData(creator(entityModel.getMvcData()).builder().regionName(regionModelData.getName()).build());
                            return entityModel;
                        }).forEach(regionModel::addEntity);
            }

            return regionModel;
        } catch (ReflectiveOperationException | DxaException e) {
            throw new SemanticMappingException("Exception happened while creating Region " + regionModelData, e);
        }
    }

    private RegionModel createRegionModel(RegionModelData regionModelData, Class<? extends ViewModel> viewModelType) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        String name = dashify(regionModelData.getName());
        return (RegionModel) viewModelType.getConstructor(String.class).newInstance(name);
    }

    private EntityModel createEntityModel(EntityModelData entityModelData, ConditionalKeyBuilder cacheRequest) {
        try {
            EntityModel entityModel = modelBuilderPipeline.createEntityModel(entityModelData);
            if (isNeverCached(entityModel)) {
                cacheRequest.skipCaching(true);
            }
            return entityModel;
        } catch (Exception e) {
            String message = "Cannot create an entity model for model data " + entityModelData;
            log.error(message, e);
            return new ExceptionEntity(e);
        }
    }

    private boolean isNeverCached(@NotNull Object object) {
        return object.getClass().isAnnotationPresent(NeverCached.class);
    }
}

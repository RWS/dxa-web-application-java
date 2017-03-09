package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.BinaryContentData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.ExternalContentData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.dxa.tridion.mapping.converter.RichTextLinkResolver;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
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
import com.sdl.webapp.common.util.TcmUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;
import static com.sdl.webapp.common.util.StringUtils.dashify;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Default implementation of {@link EntityModelBuilder} and {@link PageModelBuilder}.
 */
@Slf4j
@Service
@R2
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
    private RichTextLinkResolver richTextLinkResolver;

    @Autowired
    private LinkResolver linkResolver;

    @Autowired
    private List<ConditionalEntityEvaluator> entityEvaluators;

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public <T extends EntityModel> T buildEntityModel(@Nullable T originalEntityModel, EntityModelData modelData,
                                                      @Nullable Class<T> expectedClass) throws DxaException {
        T entityModel;
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

            //noinspection unchecked
            entityModel = (T) createViewModel(modelType, modelData);
            entityModel.setMvcData(mvcData);

            ((AbstractEntityModel) entityModel).setId(modelData.getId());
            fillViewModel(entityModel, modelData);

            _processMediaItem(modelData, entityModel);
        } catch (DxaException e) {
            throw new DxaException("Exception happened while creating a entity model from: " + modelData, e);
        }
        return entityModel;
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
    private <T extends ViewModel> T createViewModel(Class<T> viewModelType, ViewModelData modelData) throws DxaException {
        Localization localization = webRequestContext.getLocalization();
        SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(modelData.getSchemaId()));
        try {
            return semanticMapper.createEntity(viewModelType, semanticSchema.getSemanticFields(),
                    DefaultSemanticFieldDataProvider.getFor(modelData, semanticSchema));
        } catch (SemanticMappingException e) {
            log.warn("Cannot do a semantic mapping for class '{}', model data '{}', localization '{}'", viewModelType, modelData, localization);
            throw e;
        }
    }

    private <T extends EntityModel> void _processMediaItem(EntityModelData modelData, T entityModel) throws DxaException {
        if (entityModel instanceof MediaItem) {
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
    }

    @Override
    public PageModel buildPageModel(@Nullable PageModel originalPageModel, PageModelData modelData, PageInclusion includePageRegions) {
        PageModel pageModel = instantiatePageModel(originalPageModel, modelData);

        fillViewModel(pageModel, modelData);
        pageModel.setId(modelData.getId());
        pageModel.setMeta(resolveLinks(modelData.getMeta()));
        pageModel.setName(modelData.getTitle());
        pageModel.setTitle(getPageTitle(modelData));
        pageModel.setUrl(modelData.getUrlPath());

        //todo dxa2 refactor this, remove usage of deprecated method
        webRequestContext.setPage(pageModel);

        if (modelData.getRegions() != null) {
            List<RegionModelData> regions = includePageRegions == PageInclusion.EXCLUDE ?
                    filterRegionsByIncludePageUrl(modelData) : modelData.getRegions();

            regions.stream()
                    .map(this::createRegionModel)
                    .forEach(pageModel.getRegions()::add);
        }

        return pageModel;
    }

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class})
    private PageModel instantiatePageModel(@Nullable PageModel originalPageModel, PageModelData modelData) {
        MvcData mvcData = createMvcData(modelData.getMvcData(), DefaultsMvcData.PAGE);
        log.debug("MvcData '{}' for PageModel {}", mvcData, modelData);

        PageModel pageModel = originalPageModel;
        if (originalPageModel != null) {
            log.warn("Original page model expected to be null but it's '{}'", originalPageModel);
        } else {
            try {
                Class<? extends ViewModel> viewModelType = viewModelRegistry.getViewModelType(mvcData);

                log.debug("Instantiating a PageModel without a SchemaID = null, modelData = {}, view model type = '{}'", modelData, viewModelType);
                if (modelData.getSchemaId() == null) { //schema ID is not set, can't do semantic mapping
                    pageModel = viewModelType == null ? new DefaultPageModel() : (PageModel) viewModelType.newInstance();
                } else { // semantic mapping is possible, let's do it
                    pageModel = (PageModel) createViewModel(viewModelType, modelData);
                }
                pageModel.setMvcData(mvcData);
            } catch (DxaException e) {
                log.warn("Exception happened while creating a page model {}", modelData, e);
            }
        }
        return pageModel;
    }

    private void fillViewModel(ViewModel viewModel, ViewModelData modelData) {
        if (modelData.getExtensionData() != null) {
            modelData.getExtensionData().forEach(viewModel::addExtensionData);
        }

        if (viewModel instanceof AbstractViewModel && modelData.getXpmMetadata() != null) {
            ((AbstractViewModel) viewModel).setXpmMetadata(modelData.getXpmMetadata());
        }

        viewModel.setHtmlClasses(modelData.getHtmlClasses());
    }

    private Map<String, String> resolveLinks(Map<String, String> mapToResolve) {
        if (mapToResolve == null) {
            return Collections.emptyMap();
        }

        return mapToResolve.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o ->
                        TcmUtils.isTcmUri(o.getValue()) ?
                                ofNullable(linkResolver.resolveLink(o.getValue(), webRequestContext.getLocalization().getId(), true))
                                        .orElse("") :
                                richTextLinkResolver.processFragment(o.getValue())));
    }

    private String getPageTitle(PageModelData modelData) {
        Localization localization = webRequestContext.getLocalization();
        String separator = localization.getResource("core.pageTitleSeparator");
        String postfix = localization.getResource("core.pageTitlePostfix");
        log.trace("Model page title '{}', pageTitleSeparator '{}', postfix '{}'", modelData.getTitle(), separator, postfix);
        return modelData.getTitle() + separator + postfix;
    }

    private List<RegionModelData> filterRegionsByIncludePageUrl(PageModelData modelData) {
        return modelData.getRegions().stream()
                .filter(regionModelData -> isBlank(regionModelData.getIncludePageUrl()))
                .collect(Collectors.toList());
    }

    private RegionModel createRegionModel(RegionModelData regionModelData) {
        MvcData mvcData = createMvcData(regionModelData.getMvcData(), DefaultsMvcData.REGION);
        log.debug("MvcData '{}' for RegionModel {}", mvcData, regionModelData);

        try {
            Class<? extends ViewModel> viewModelType = viewModelRegistry.getViewModelType(mvcData);
            if (viewModelType == null) {
                throw new DxaException("Cannot find a view model type for " + mvcData);
            }

            RegionModel regionModel = (RegionModel) viewModelType.getConstructor(String.class)
                    .newInstance(dashify(regionModelData.getName()));
            fillViewModel(regionModel, regionModelData);
            regionModel.setMvcData(mvcData);
            ((AbstractViewModel) regionModel).setXpmMetadata(regionModel.getXpmMetadata());

            if (regionModelData.getRegions() != null) {
                regionModelData.getRegions().stream()
                        .map(this::createRegionModel)
                        .forEach(regionModel.getRegions()::add);
            }

            if (regionModelData.getEntities() != null) {
                regionModelData.getEntities().stream()
                        .map(entityModelData -> {
                            EntityModel entityModel = createEntityModel(entityModelData);
                            entityModel.setMvcData(creator(entityModel.getMvcData()).builder().regionName(regionModelData.getName()).build());
                            return entityModel;
                        })
                        .filter(entityModel -> entityEvaluators.stream().allMatch(evaluator -> evaluator.includeEntity(entityModel)))
                        .forEach(regionModel::addEntity);
            }

            return regionModel;
        } catch (DxaException e) {
            log.warn("Exception happened while creating Region {}", regionModelData, e);
            return null;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            log.warn("Cannot instantiate a region model '{}' because of problems with reflective access", regionModelData, e);
            return null;
        }
    }

    private EntityModel createEntityModel(EntityModelData entityModelData) {
        try {
            return modelBuilderPipeline.createEntityModel(entityModelData);
        } catch (Exception e) {
            log.warn("Cannot create an entity model for model data {}", entityModelData, e);
            return new ExceptionEntity(e);
        }
    }
}

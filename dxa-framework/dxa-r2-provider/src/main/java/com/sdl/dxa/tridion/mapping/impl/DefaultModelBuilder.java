package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.webapp.common.api.WebRequestContext;
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
import com.sdl.webapp.common.api.model.entity.ExceptionEntity;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;
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

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public <T extends EntityModel> T buildEntityModel(@Nullable EntityModel originalEntityModel, EntityModelData modelData,
                                                      @Nullable Class<T> expectedClass) {
        MvcData mvcData = createMvcData(modelData.getMvcData(), DefaultsMvcData.ENTITY);
        T entityModel = null;
        try {
            Class<? extends ViewModel> modelType;
            if (expectedClass != null) {
                log.debug("Expected class is pre-set to {} for model {}", expectedClass, modelData);
                // https://jira.sdl.com/browse/TSI-2273
                // we currently ignore the base type because of the issue but don't ignore the fact that it's set
                SemanticSchema semanticSchema = webRequestContext.getLocalization().getSemanticSchemas().get(Long.parseLong(modelData.getSchemaId()));
                modelType = viewModelRegistry.getMappedModelTypes(semanticSchema.getFullyQualifiedNames());
            } else {
                log.debug("Expected class is not set explicitly, trying to get it from MvcData");
                modelType = viewModelRegistry.getViewModelType(mvcData);
            }

            //noinspection unchecked
            entityModel = (T) createViewModel(modelType, modelData);
            ((AbstractEntityModel) entityModel).setId(modelData.getId());
            entityModel.setMvcData(mvcData);
        } catch (DxaException | SemanticMappingException e) {
            log.warn("Exception happened while creating a page model {}", modelData, e);
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
    private <T extends ViewModel> T createViewModel(Class<T> viewModelType, ViewModelData modelData) throws SemanticMappingException, DxaException {
        Localization localization = webRequestContext.getLocalization();
        SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(modelData.getSchemaId()));
        try {
            return semanticMapper.createEntity(viewModelType, semanticSchema.getSemanticFields(), DefaultSemanticFieldDataProvider.getFor(modelData));
        } catch (SemanticMappingException e) {
            log.warn("Cannot do a semantic mapping for class '{}', model data '{}', localization '{}'", viewModelType, modelData, localization);
            throw e;
        }
    }

    @Override
    public PageModel buildPageModel(@Nullable PageModel originalPageModel, PageModelData modelData, PageInclusion includePageRegions) {
        PageModel pageModel = instantiatePageModel(originalPageModel, modelData);

        fillViewModel(pageModel, modelData);
        pageModel.setId(modelData.getId());
        pageModel.setMeta(modelData.getMeta()); //todo ResolveMetaLinks(pageModelData.Meta)
        pageModel.setTitle(modelData.getTitle());

        if (modelData.getRegions() != null) {
            List<RegionModelData> regions = includePageRegions != PageInclusion.INCLUDE ?
                    modelData.getRegions() :
                    modelData.getRegions().stream()
                            .filter(regionModelData -> isBlank(regionModelData.getIncludePageUrl()))
                            .collect(Collectors.toList());

            pageModel.getRegions().addAll(regions.stream()
                    .map(this::createRegionModel)
                    .collect(Collectors.toSet()));
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
            } catch (DxaException | SemanticMappingException e) {
                log.warn("Exception happened while creating a page model {}", modelData, e);
            }
        }
        return pageModel;
    }

    private void fillViewModel(ViewModel viewModel, ViewModelData modelData) {
        if (modelData.getExtensionData() != null) {
            modelData.getExtensionData().forEach(viewModel::addExtensionData);
        }
        viewModel.setHtmlClasses(modelData.getHtmlClasses());
    }

    private RegionModel createRegionModel(RegionModelData regionModelData) {
        MvcData mvcData = createMvcData(regionModelData.getMvcData(), DefaultsMvcData.REGION);
        log.debug("MvcData '{}' for RegionModel {}", mvcData, regionModelData);

        try {
            Class<? extends ViewModel> viewModelType = viewModelRegistry.getViewModelType(mvcData);
            RegionModel regionModel = (RegionModel) viewModelType.getConstructor(String.class).newInstance(regionModelData.getName());
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
                        .map(entityModelData -> createEntityModel(entityModelData, regionModelData))
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

    private EntityModel createEntityModel(EntityModelData entityModelData, RegionModelData regionModelData) {
        try {
            EntityModel entityModel = modelBuilderPipeline.createEntityModel(entityModelData);
            entityModel.setMvcData(creator(entityModel.getMvcData()).builder().regionName(regionModelData.getName()).build());
            return entityModel;
        } catch (Exception e) {
            log.warn("Exception happened while building {}", entityModelData, e);
            return new ExceptionEntity(e);
        }
    }
}

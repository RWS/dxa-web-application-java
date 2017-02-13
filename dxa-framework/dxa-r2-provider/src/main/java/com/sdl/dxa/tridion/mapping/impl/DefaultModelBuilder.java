package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link EntityModelBuilder} and {@link PageModelBuilder}.
 */
@Slf4j
@Service
public class DefaultModelBuilder implements EntityModelBuilder, PageModelBuilder {

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private SemanticMapper semanticMapper;

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public EntityModel buildEntityModel(@Nullable EntityModel originalEntityModel, EntityModelData modelData, Localization localization) {
        return null;
    }

    @Override
    public EntityModel buildEntityModel(@Nullable EntityModel originalEntityModel, EntityModelData modelData, @Nullable Class<? extends EntityModel> expectedClass, Localization localization) {
        return null;
    }

    @Override

    public PageModel buildPageModel(@Nullable PageModel originalPageModel, PageModelData modelData, PageInclusion includePageRegions, Localization localization) {
        PageModel pageModel = instantiatePageModel(originalPageModel, modelData, localization);

        pageModel.setId(modelData.getId());
        modelData.getExtensionData().forEach(pageModel::addExtensionData);
        pageModel.setHtmlClasses(modelData.getHtmlClasses());
        pageModel.setXpmMetadata(modelData.getXpmMetadata());

        pageModel.setMeta(modelData.getMeta()); //todo ResolveMetaLinks(pageModelData.Meta)
        pageModel.setTitle(modelData.getTitle());

        if (modelData.getRegions() != null) {
            //todo
//            IEnumerable<RegionModelData> regions = includePageRegions ? pageModelData.Regions : pageModelData.Regions.Where(r => r.IncludePageUrl == null);
//            pageModel.Regions.UnionWith(regions.Select(data => CreateRegionModel(data, localization)));
        }

        return pageModel;
    }

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class})
    private PageModel instantiatePageModel(@Nullable PageModel originalPageModel, PageModelData modelData, Localization localization) {
        MvcData mvcData = createMvcData(modelData.getMvcData(), DefaultsMvcData.PAGE);
        log.debug("MvcData '' for PageModel {}", mvcData, modelData);

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
                    pageModel = (PageModel) createViewModel(viewModelType, modelData, localization);
                }
            } catch (DxaException e) {
                e.printStackTrace();
            } catch (SemanticMappingException e) {
                e.printStackTrace();
            }
            pageModel.setMvcData(mvcData);
        }
        return pageModel;
    }

    @NotNull
    private MvcData createMvcData(MvcModelData modelData, DefaultsMvcData defaults) {
        return MvcDataCreator.creator(MvcDataImpl.newBuilder()
                .actionName(modelData.getActionName())
                .areaName(modelData.getAreaName())
                .controllerAreaName(modelData.getControllerAreaName())
                .controllerName(modelData.getControllerName())
                .viewName(modelData.getViewName())
                .routeValues(modelData.getParameters())
        ).defaults(defaults).create();
    }

    @NotNull
    private <T extends ViewModel> T createViewModel(Class<T> viewModelType, ViewModelData modelData, Localization localization) throws SemanticMappingException {
        SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(modelData.getSchemaId()));
        try {
            return semanticMapper.createEntity(viewModelType, semanticSchema.getSemanticFields(), DefaultSemanticFieldDataProvider.getFor(modelData));
        } catch (SemanticMappingException e) {
            log.warn("Cannot do a semantic mapping for class '{}', model data '{}', localization '{}'", viewModelType, modelData, localization);
            throw e;
        }
    }
}

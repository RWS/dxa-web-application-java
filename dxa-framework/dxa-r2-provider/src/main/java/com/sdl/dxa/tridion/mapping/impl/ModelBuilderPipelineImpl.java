package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Each {@linkplain ModelBuilder Model Builder} in the pipeline is invoked and has the possibility to modify the resulting Model.
 * If the Model Builder gets {@code null}, it has to construct the View Model.
 * {@linkplain DefaultModelBuilder} should normally be the first.
 *
 * @see EntityModelBuilder
 * @see PageModelBuilder
 */
@Service("r2modelBuilder")
@R2
public class ModelBuilderPipelineImpl implements ModelBuilderPipeline {

    private List<EntityModelBuilder> entityModelBuilders;

    private List<PageModelBuilder> pageModelBuilders;

    @Autowired
    public void setEntityModelBuilders(List<EntityModelBuilder> entityModelBuilders) {
        this.entityModelBuilders = entityModelBuilders;
    }

    @Autowired
    public void setPageModelBuilders(List<PageModelBuilder> pageModelBuilders) {
        this.pageModelBuilders = pageModelBuilders;
    }

    @Override
    @Nullable
    public PageModel createPageModel(@NotNull PageModelData modelData,
                                     @NotNull PageInclusion includePageRegions,
                                     @NotNull Localization localization) {
        PageModel pageModel = null;
        for (PageModelBuilder builder : pageModelBuilders) {
            pageModel = builder.buildPageModel(pageModel, modelData, includePageRegions, localization);
        }
        return pageModel;
    }

    @Override
    @Nullable
    public EntityModel createEntityModel(@NotNull EntityModelData modelData, @NotNull Localization localization) {
        return createEntityModel(modelData, null, localization);
    }

    @Nullable
    @Override
    public <T extends EntityModel> T createEntityModel(@NotNull EntityModelData modelData, @Nullable Class<T> expectedClass, @NotNull Localization localization) {
        T entityModel = null;
        for (EntityModelBuilder builder : entityModelBuilders) {
            entityModel = builder.buildEntityModel(entityModel, modelData, expectedClass, localization);
        }
        return entityModel;
    }
}

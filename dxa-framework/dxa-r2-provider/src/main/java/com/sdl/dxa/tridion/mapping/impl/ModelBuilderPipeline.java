package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilder;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Represents a pipeline/chain of configured Strongly Typed View Model Builders based on DXA R2 Data Model.
 * <p>Each {@linkplain ModelBuilder Model Builder} in the pipeline is invoked and has the possibility to modify the resulting Model.
 * If the Model Builder gets {@code null}, it has to construct the View Model.
 * {@linkplain DefaultModelBuilder} should normally be the first.</p>
 *
 * @see EntityModelBuilder
 * @see PageModelBuilder
 */
@Service
@R2
public class ModelBuilderPipeline {

    private final List<EntityModelBuilder> entityModelBuilders;

    private final List<PageModelBuilder> pageModelBuilders;

    @Autowired
    public ModelBuilderPipeline(List<EntityModelBuilder> entityModelBuilders, List<PageModelBuilder> pageModelBuilders) {
        this.entityModelBuilders = entityModelBuilders == null ? Collections.emptyList() : entityModelBuilders;
        this.pageModelBuilders = pageModelBuilders == null ? Collections.emptyList() : pageModelBuilders;
    }

    /**
     * See {@link PageModelBuilder#buildPageModel(PageModel, PageModelData, PageInclusion, Localization)}.
     *
     * @return Page Model or {@code null} if no builders are registered
     */
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

    /**
     * See {@link EntityModelBuilder#buildEntityModel(EntityModel, EntityModelData, Localization)}.
     *
     * @return Entity Model or {@code null} if no builders are registered
     */
    @Nullable
    public EntityModel createEntityModel(@NotNull EntityModelData modelData, @NotNull Localization localization) {
        return createEntityModel(modelData, null, localization);
    }

    /**
     * See {@link EntityModelBuilder#buildEntityModel(EntityModel, EntityModelData, Class, Localization)}.
     *
     * @return Entity Model or {@code null} if no builders are registered
     */
    @Nullable
    public EntityModel createEntityModel(@NotNull EntityModelData modelData, @Nullable Class<? extends EntityModel> expectedClass, @NotNull Localization localization) {
        EntityModel entityModel = null;
        for (EntityModelBuilder builder : entityModelBuilders) {
            entityModel = expectedClass == null ?
                    builder.buildEntityModel(entityModel, modelData, localization) :
                    builder.buildEntityModel(entityModel, modelData, expectedClass, localization);
        }
        return entityModel;
    }
}

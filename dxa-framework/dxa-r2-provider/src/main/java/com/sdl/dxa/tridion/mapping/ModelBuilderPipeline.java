package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a pipeline/chain of configured Strongly Typed View Model Builders based on DXA R2 Data Model.
 */
public interface ModelBuilderPipeline {

    /**
     * See {@link PageModelBuilder#buildPageModel(PageModel, PageModelData, PageInclusion, Localization)}.
     *
     * @return Page Model or {@code null} if no builders are registered
     */
    @Nullable
    PageModel createPageModel(@NotNull PageModelData modelData,
                              @NotNull PageInclusion includePageRegions,
                              @NotNull Localization localization);

    /**
     * See {@link EntityModelBuilder#buildEntityModel(EntityModel, EntityModelData, Class, Localization)}.
     * {@code expectedClass} defaults to data from {@code MvcData}
     *
     * @return Entity Model or {@code null} if no builders are registered
     */
    @Nullable
    EntityModel createEntityModel(@NotNull EntityModelData modelData, @NotNull Localization localization);

    /**
     * See {@link EntityModelBuilder#buildEntityModel(EntityModel, EntityModelData, Class, Localization)}.
     *
     * @return Entity Model or {@code null} if no builders are registered
     */
    @Nullable
    <T extends EntityModel> T createEntityModel(@NotNull EntityModelData modelData,
                                                @Nullable Class<T> expectedClass,
                                                @NotNull Localization localization);
}

package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.exceptions.DxaException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a pipeline/chain of configured Strongly Typed View Model Builders based on DXA R2 Data Model.
 *
 * @dxa.publicApi
 */
public interface ModelBuilderPipeline {

    /**
     * See {@link PageModelBuilder#buildPageModel(PageModel, PageModelData)}.
     *
     * @return Page Model or {@code null} if no builders are registered
     */
    @NotNull
    PageModel createPageModel(@NotNull PageModelData modelData);

    /**
     * See {@link EntityModelBuilder#buildEntityModel(EntityModel, EntityModelData, Class)}.
     * {@code expectedClass} defaults to data from {@code MvcData}
     *
     * @return Entity Model or {@code null} if no builders are registered
     */
    @NotNull
    <T extends EntityModel> T createEntityModel(@NotNull EntityModelData modelData) throws DxaException;

    /**
     * See {@link EntityModelBuilder#buildEntityModel(EntityModel, EntityModelData, Class)}.
     *
     * @return Entity Model
     * @throws IllegalArgumentException in case
     */
    @NotNull
    <T extends EntityModel> T createEntityModel(@NotNull EntityModelData modelData, @Nullable Class<T> expectedClass) throws DxaException;
}

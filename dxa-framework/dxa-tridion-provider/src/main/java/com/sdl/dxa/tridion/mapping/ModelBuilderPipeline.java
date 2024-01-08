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
 */
public interface ModelBuilderPipeline {

    /**
     * See {@link PageModelBuilder#buildPageModel(PageModel, PageModelData)}.
     *
     * @param modelData model data
     * @return Page Model or {@code null} if no builders are registered
     */
    @NotNull
    PageModel createPageModel(@NotNull PageModelData modelData);

    /**
     * See {@link EntityModelBuilder#buildEntityModel(EntityModel, EntityModelData, Class)}.
     * {@code expectedClass} defaults to data from {@code MvcData}
     *
     * @param modelData model data
     * @return Entity Model or {@code null} if no builders are registered
     * @throws DxaException in case
     */
    @NotNull
    <T extends EntityModel> T createEntityModel(@NotNull EntityModelData modelData) throws DxaException;

    /**
     * See {@link EntityModelBuilder#buildEntityModel(EntityModel, EntityModelData, Class)}.
     *
     * @param <T> type that extends EntityModel
     * @param modelData model data
     * @param expectedClass expected class
     * @return Entity Model
     * @throws DxaException in case
     */
    @NotNull
    <T extends EntityModel> T createEntityModel(@NotNull EntityModelData modelData, @Nullable Class<T> expectedClass) throws DxaException;
}

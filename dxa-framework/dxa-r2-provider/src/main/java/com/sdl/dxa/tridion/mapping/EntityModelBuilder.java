package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.tridion.mapping.impl.ModelBuilderPipeline;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a strongly typed {@linkplain EntityModel Entity Model} based on a given DXA R2 Data Model.
 *
 * @see ModelBuilder
 */
public interface EntityModelBuilder extends ModelBuilder {

    /**
     * {@code Class<T> expectedClass} defaults to data in {@linkplain MvcModelData MVC data} of {@code modelData}.
     * See {@link #buildEntityModel(EntityModel, EntityModelData, Class, Localization)}.
     */
    @Contract("_, _, _ -> !null")
    EntityModel buildEntityModel(@Nullable EntityModel originalEntityModel, EntityModelData modelData, Localization localization);

    /**
     * Builds a strongly typed Entity Model based on a given DXA R2 Data Model. Never returns {@code null}.
     *
     * @param originalEntityModel the strongly typed {@linkplain EntityModel Entity Model} to build.
     *                            Is {@code null} for the first {@linkplain EntityModelBuilder Entity Model Builder} in the {@link ModelBuilderPipeline}
     * @param modelData           the DXA R2 Data Model
     * @param expectedClass       required class of entity model, gets the priority if {@code modelData} contains {@linkplain MvcModelData MVC} data
     * @param localization        the context {@link Localization}
     * @return the strongly typed Entity Model
     */
    @Contract("_, _, _, _ -> !null")
    EntityModel buildEntityModel(@Nullable EntityModel originalEntityModel, EntityModelData modelData,
                                 @Nullable Class<? extends EntityModel> expectedClass,
                                 Localization localization);
}

package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.tridion.mapping.impl.ModelBuilderPipelineImpl;
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
     * Builds a strongly typed Entity Model based on a given DXA R2 Data Model. Never returns {@code null}.
     *
     * @param originalEntityModel the strongly typed {@linkplain EntityModel Entity Model} to build.
     *                            Is {@code null} for the first {@linkplain EntityModelBuilder Entity Model Builder} in the {@link ModelBuilderPipelineImpl}
     * @param modelData           the DXA R2 Data Model
     * @param expectedClass       required class of entity model, gets the priority if {@code modelData} contains {@linkplain MvcModelData MVC} data
     * @return the strongly typed Entity Model
     */
    @Contract("_, _, _ -> !null")
    <T extends EntityModel> T buildEntityModel(@Nullable EntityModel originalEntityModel, EntityModelData modelData,
                                               @Nullable Class<T> expectedClass);
}

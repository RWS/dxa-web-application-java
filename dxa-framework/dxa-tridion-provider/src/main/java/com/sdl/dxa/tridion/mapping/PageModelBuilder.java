package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.impl.ModelBuilderPipelineImpl;
import com.sdl.webapp.common.api.model.PageModel;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a strongly typed {@linkplain PageModel Page Model} based on a given DXA R2 Data Model.
 *
 * @dxa.publicApi
 * @see ModelBuilder
 */
public interface PageModelBuilder extends ModelBuilder {

    /**
     * Builds a strongly typed Page Model from a given DXA R2 Data Model.
     *
     * @param originalPageModel the strongly typed {@linkplain PageModel Page Model} to build.
     *                          Is {@code null} for the first {@linkplain PageModelBuilder Page Model Builder} in the {@link ModelBuilderPipelineImpl}
     * @param modelData         the DXA R2 Data Model
     * @return the strongly typed Page Model, or {@code null} if cannot build a page
     */
    @Nullable
    PageModel buildPageModel(@Nullable PageModel originalPageModel, PageModelData modelData);
}

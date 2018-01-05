package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * Each {@linkplain ModelBuilder Model Builder} in the pipeline is invoked and has the possibility to modify the resulting Model.
 * If the Model Builder gets {@code null}, it has to construct the View Model.
 * {@linkplain DefaultModelBuilder} should normally be the first.
 *
 * @dxa.publicApi
 * @see EntityModelBuilder
 * @see PageModelBuilder
 */
@Service("r2modelBuilder")
@Slf4j
public class ModelBuilderPipelineImpl implements ModelBuilderPipeline {

    private List<EntityModelBuilder> entityModelBuilders = Collections.emptyList();

    private List<PageModelBuilder> pageModelBuilders = Collections.emptyList();

    @Autowired
    public void setEntityModelBuilders(List<EntityModelBuilder> entityModelBuilders) {
        this.entityModelBuilders = entityModelBuilders;
    }

    @Autowired
    public void setPageModelBuilders(List<PageModelBuilder> pageModelBuilders) {
        this.pageModelBuilders = pageModelBuilders;
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @Override
    @NotNull
    public PageModel createPageModel(@NotNull PageModelData modelData) {
        PageModel pageModel = null;
        for (PageModelBuilder builder : pageModelBuilders) {
            pageModel = builder.buildPageModel(pageModel, modelData);
        }
        Assert.notNull(pageModel, "Page Model is null after model pipeline, model builder are not set?");
        return pageModel; //NOSONAR
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @Override
    @NotNull
    public <T extends EntityModel> T createEntityModel(@NotNull EntityModelData modelData) throws DxaException {
        return createEntityModel(modelData, null);
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @NotNull
    @Override
    public <T extends EntityModel> T createEntityModel(@NotNull EntityModelData modelData, @Nullable Class<T> expectedClass) throws DxaException {
        T entityModel = null;
        for (EntityModelBuilder builder : entityModelBuilders) {
            entityModel = builder.buildEntityModel(entityModel, modelData, expectedClass);
        }
        Assert.notNull(entityModel, "Entity Model is null after model pipeline, model builder are not set?");
        return entityModel; //NOSONAR
    }
}

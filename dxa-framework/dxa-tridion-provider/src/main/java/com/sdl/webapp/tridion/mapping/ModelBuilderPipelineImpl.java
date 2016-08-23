package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * This implementation calls all known builders of types {@link PageBuilder} or {@link EntityBuilder} for pages and entities respectively
 * in the predefined order. Order is defined by builders themselves because of implementing {@link Ordered} interface.
 */
@org.springframework.stereotype.Component
@Slf4j
public class ModelBuilderPipelineImpl implements ModelBuilderPipeline {

    private List<PageBuilder> pageBuilderHandlers;

    private List<EntityBuilder> entityBuilderHandlers;

    @Autowired
    public void setPageBuilderHandlers(List<PageBuilder> handlers) {
        this.pageBuilderHandlers = handlers;
    }

    @Autowired
    public void setEntityBuilderHandlers(List<EntityBuilder> handlers) {
        this.entityBuilderHandlers = handlers;
    }

    @PostConstruct
    public void init() {
        Collections.sort(pageBuilderHandlers, AnnotationAwareOrderComparator.INSTANCE);
        Collections.sort(entityBuilderHandlers, AnnotationAwareOrderComparator.INSTANCE);
    }

    @Override
    public <T extends PageModel> T createPageModel(Page genericPage, Localization localization, ContentProvider contentProvider) throws ContentProviderException {
        T pageModel = null;
        for (PageBuilder pageBuilder : pageBuilderHandlers) {
            log.debug("Invoking {} on {}", pageBuilder.getClass(), genericPage.getFileName());
            pageModel = pageBuilder.createPage(genericPage, pageModel, localization, contentProvider);
        }
        return pageModel;
    }

    @Override
    public <T extends EntityModel> T createEntityModel(final ComponentPresentation componentPresentation, final Localization localization) throws ContentProviderException {
        return createEntityModelInternal(new Strategy<T>() {
            @Override
            public T apply(T entity, EntityBuilder entityBuilder) throws ContentProviderException {
                return entityBuilder.createEntity(componentPresentation, entity, localization);
            }
        });
    }

    @Override
    public <T extends EntityModel> T createEntityModel(final Component component, final Localization localization) throws ContentProviderException {
        return createEntityModelInternal(new Strategy<T>() {
            @Override
            public T apply(T entity, EntityBuilder entityBuilder) throws ContentProviderException {
                return entityBuilder.createEntity(component, entity, localization);
            }
        });
    }

    @Override
    public <T extends EntityModel> T createEntityModel(final Component component, final Localization localization, final Class<T> entityClass) throws ContentProviderException {
        return createEntityModelInternal(new Strategy<T>() {
            @Override
            public T apply(T entity, EntityBuilder entityBuilder) throws ContentProviderException {
                return entityBuilder.createEntity(component, entity, localization, entityClass);
            }
        });
    }

    private <T extends EntityModel> T createEntityModelInternal(Strategy<T> strategy) throws ContentProviderException {
        T entityModel = null;
        for (EntityBuilder entityBuilder : entityBuilderHandlers) {
            entityModel = strategy.apply(entityModel, entityBuilder);
        }
        return entityModel;
    }

    private interface Strategy<T> {

        T apply(T entity, EntityBuilder builder) throws ContentProviderException;
    }
}

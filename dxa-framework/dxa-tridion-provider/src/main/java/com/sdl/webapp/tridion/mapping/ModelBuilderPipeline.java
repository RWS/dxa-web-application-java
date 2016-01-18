package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Component
public class ModelBuilderPipeline {

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

    public PageModel createPageModel(org.dd4t.contentmodel.Page genericPage, Localization localization, ContentProvider contentProvider) throws ContentProviderException {
        PageModel pageModel = null;
        for (PageBuilder pageBuilder : pageBuilderHandlers) {
            pageModel = pageBuilder.createPage(genericPage, pageModel, localization, contentProvider);
        }
        return pageModel;
    }

    public EntityModel createEntityModel(ComponentPresentation cp, Localization localization) throws ContentProviderException {
        EntityModel entityModel = null;
        for (EntityBuilder entityBuilder : entityBuilderHandlers) {
            entityModel = entityBuilder.createEntity(cp, entityModel, localization);
        }
        return entityModel;
    }

    public EntityModel createEntityModel(Component component, Localization localization) throws ContentProviderException {
        EntityModel entityModel = null;
        for (EntityBuilder entityBuilder : entityBuilderHandlers) {
            entityModel = entityBuilder.createEntity(component, entityModel, localization);
        }
        return entityModel;
    }

    public EntityModel createEntityModel(Component component, Localization localization, Class<AbstractEntityModel> entityClass) throws ContentProviderException {
        EntityModel entityModel = null;
        for (EntityBuilder entityBuilder : entityBuilderHandlers) {
            entityModel = entityBuilder.createEntity(component, entityModel, localization, entityClass);
        }
        return entityModel;
    }
}

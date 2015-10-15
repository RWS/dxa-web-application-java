package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;

import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;

import java.util.List;

/**
 * Created by Administrator on 15/09/2015.
 */

public class ModelBuilderPipeline {

    private List<PageBuilder> pageBuilderHandlers;

    private List<EntityBuilder> entityBuilderHandlers;

    public void setPageBuilderHandlers(List<PageBuilder> handlers){
        this.pageBuilderHandlers = handlers;
    }

    public void setEntityBuilderHandlers(List<EntityBuilder> handlers){
        this.entityBuilderHandlers = handlers;
    }

    public PageModel CreatePageModel(org.dd4t.contentmodel.Page genericPage, Localization localization, ContentProvider contentProvider) throws ContentProviderException {
        PageModel pageModel = null;
        for(PageBuilder pageBuilder :pageBuilderHandlers)
        {
            pageModel = pageBuilder.createPage(genericPage, pageModel, localization, contentProvider);
        }
        return pageModel;
    }

    public EntityModel CreateEntityModel(ComponentPresentation cp, Localization localization) throws ContentProviderException {
        EntityModel entityModel = null;
        for(EntityBuilder entityBuilder : entityBuilderHandlers)
        {
            entityModel = entityBuilder.createEntity(cp, entityModel, localization);
        }
        return entityModel;
    }

    public EntityModel CreateEntityModel(Component component, Localization localization) throws ContentProviderException {
        EntityModel entityModel = null;
        for(EntityBuilder entityBuilder : entityBuilderHandlers)
        {
            entityModel = entityBuilder.createEntity(component, entityModel, localization);
        }
        return entityModel;
    }

    public EntityModel CreateEntityModel(Component component, Localization localization, Class<AbstractEntityModel> entityClass) throws ContentProviderException {
        EntityModel entityModel = null;
        for(EntityBuilder entityBuilder : entityBuilderHandlers)
        {
            entityModel = entityBuilder.createEntity(component, entityModel, localization, entityClass);
        }
        return entityModel;
    }
}

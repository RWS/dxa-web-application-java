package com.sdl.dxa.modules.test;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.dd4t.EntityBuilder;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;

public class CustomEntityBuilder implements EntityBuilder {
    @Override
    public EntityModel createEntity(ComponentPresentation componentPresentation, EntityModel originalEntityModel, Localization localization) throws ContentProviderException {
        originalEntityModel.setHtmlClasses(originalEntityModel.getHtmlClasses() + " customclassbycustomentitybuilder");
        return originalEntityModel;
    }

    @Override
    public EntityModel createEntity(Component component, EntityModel originalEntityModel, Localization localization) throws ContentProviderException {
        originalEntityModel.setHtmlClasses(originalEntityModel.getHtmlClasses() + " customclassbycustomentitybuilder");
        return originalEntityModel;
    }

    @Override
    public EntityModel createEntity(Component component, EntityModel originalEntityModel, Localization localization, Class<AbstractEntityModel> entityClass) throws ContentProviderException {
        originalEntityModel.setHtmlClasses(originalEntityModel.getHtmlClasses() + " customclassbycustomentitybuilder");
        return originalEntityModel;
    }
}

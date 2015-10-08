package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.dd4t.contentmodel.ComponentPresentation;

/**
 * Entity Builder interface
 */
public interface EntityBuilder {
    EntityModel createEntity(ComponentPresentation componentPresentation, EntityModel originalEntityModel, Localization localization)
            throws ContentProviderException;

    EntityModel createEntity(org.dd4t.contentmodel.Component component, EntityModel originalEntityModel, Localization localization)
            throws ContentProviderException;

    EntityModel createEntity(org.dd4t.contentmodel.Component component, EntityModel originalEntityModel, Localization localization, Class<AbstractEntityModel> entityClass)
            throws ContentProviderException;

}

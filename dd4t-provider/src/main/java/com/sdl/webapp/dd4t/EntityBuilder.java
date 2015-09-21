package com.sdl.webapp.dd4t;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import org.dd4t.contentmodel.ComponentPresentation;

/**
 * Created by Administrator on 15/09/2015.
 */
public interface EntityBuilder {
    public EntityModel createEntity(ComponentPresentation componentPresentation, EntityModel originalEntityModel, Localization localization)
            throws ContentProviderException;

    public EntityModel createEntity(org.dd4t.contentmodel.Component component, EntityModel originalEntityModel, Localization localization)
            throws ContentProviderException;

}

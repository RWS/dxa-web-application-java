package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.springframework.core.Ordered;

public interface EntityBuilder extends Ordered {

    <T extends EntityModel> T createEntity(ComponentPresentation componentPresentation, T originalEntityModel, Localization localization)
            throws ContentProviderException;

    <T extends EntityModel> T createEntity(Component component, T originalEntityModel, Localization localization)
            throws ContentProviderException;

    <T extends EntityModel> T createEntity(Component component, T originalEntityModel, Localization localization, Class<T> entityClass)
            throws ContentProviderException;

}

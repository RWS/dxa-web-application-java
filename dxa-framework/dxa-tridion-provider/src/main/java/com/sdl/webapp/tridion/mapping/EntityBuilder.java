package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;

/**
 * <p>EntityBuilder interface.</p>
 */
public interface EntityBuilder {
    /**
     * <p>createEntity.</p>
     *
     * @param componentPresentation a {@link org.dd4t.contentmodel.ComponentPresentation} object.
     * @param originalEntityModel   a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @param localization          a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @return a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    EntityModel createEntity(ComponentPresentation componentPresentation, EntityModel originalEntityModel, Localization localization)
            throws ContentProviderException;

    /**
     * <p>createEntity.</p>
     *
     * @param component           a {@link org.dd4t.contentmodel.Component} object.
     * @param originalEntityModel a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @param localization        a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @return a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    EntityModel createEntity(Component component, EntityModel originalEntityModel, Localization localization)
            throws ContentProviderException;

    /**
     * <p>createEntity.</p>
     *
     * @param component a {@link org.dd4t.contentmodel.Component} object.
     * @param originalEntityModel a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @param entityClass a {@link java.lang.Class} object.
     * @return a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    EntityModel createEntity(Component component, EntityModel originalEntityModel, Localization localization, Class<AbstractEntityModel> entityClass)
            throws ContentProviderException;

}

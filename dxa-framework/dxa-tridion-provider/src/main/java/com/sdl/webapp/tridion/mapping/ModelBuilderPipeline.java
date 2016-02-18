package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Component
/**
 * <p>ModelBuilderPipeline class.</p>
 */
@Slf4j
public class ModelBuilderPipeline {

    private List<PageBuilder> pageBuilderHandlers;

    private List<EntityBuilder> entityBuilderHandlers;

    /**
     * <p>Setter for the field <code>pageBuilderHandlers</code>.</p>
     *
     * @param handlers a {@link java.util.List} object.
     */
    @Autowired
    public void setPageBuilderHandlers(List<PageBuilder> handlers) {
        this.pageBuilderHandlers = handlers;
    }

    /**
     * <p>Setter for the field <code>entityBuilderHandlers</code>.</p>
     *
     * @param handlers a {@link java.util.List} object.
     */
    @Autowired
    public void setEntityBuilderHandlers(List<EntityBuilder> handlers) {
        this.entityBuilderHandlers = handlers;
    }

    /**
     * <p>init.</p>
     */
    @PostConstruct
    public void init() {
        Collections.sort(pageBuilderHandlers, AnnotationAwareOrderComparator.INSTANCE);
        Collections.sort(entityBuilderHandlers, AnnotationAwareOrderComparator.INSTANCE);
    }

    /**
     * <p>createPageModel.</p>
     *
     * @param genericPage     a {@link org.dd4t.contentmodel.Page} object.
     * @param localization    a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @param contentProvider a {@link com.sdl.webapp.common.api.content.ContentProvider} object.
     * @return a {@link com.sdl.webapp.common.api.model.PageModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    public PageModel createPageModel(org.dd4t.contentmodel.Page genericPage, Localization localization, ContentProvider contentProvider) throws ContentProviderException {
        PageModel pageModel = null;
        for (PageBuilder pageBuilder : pageBuilderHandlers) {
            log.debug("Invoking {} on {}", pageBuilder.getClass(), genericPage.getFileName());
            pageModel = pageBuilder.createPage(genericPage, pageModel, localization, contentProvider);
        }
        return pageModel;
    }

    /**
     * <p>createEntityModel.</p>
     *
     * @param cp           a {@link org.dd4t.contentmodel.ComponentPresentation} object.
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @return a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    public EntityModel createEntityModel(ComponentPresentation cp, Localization localization) throws ContentProviderException {
        EntityModel entityModel = null;
        for (EntityBuilder entityBuilder : entityBuilderHandlers) {
            entityModel = entityBuilder.createEntity(cp, entityModel, localization);
        }
        return entityModel;
    }

    /**
     * <p>createEntityModel.</p>
     *
     * @param component    a {@link org.dd4t.contentmodel.Component} object.
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @return a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    public EntityModel createEntityModel(Component component, Localization localization) throws ContentProviderException {
        EntityModel entityModel = null;
        for (EntityBuilder entityBuilder : entityBuilderHandlers) {
            entityModel = entityBuilder.createEntity(component, entityModel, localization);
        }
        return entityModel;
    }

    /**
     * <p>createEntityModel.</p>
     *
     * @param component    a {@link org.dd4t.contentmodel.Component} object.
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @param entityClass  a {@link java.lang.Class} object.
     * @return a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    public EntityModel createEntityModel(Component component, Localization localization, Class<AbstractEntityModel> entityClass) throws ContentProviderException {
        EntityModel entityModel = null;
        for (EntityBuilder entityBuilder : entityBuilderHandlers) {
            entityModel = entityBuilder.createEntity(component, entityModel, localization, entityClass);
        }
        return entityModel;
    }
}

package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.jetbrains.annotations.Nullable;

/**
 * Model builder pipeline is responsible for constructing DXA models from DD4T representations.
 */
public interface ModelBuilderPipeline {

    /**
     * Constructs a DXA {@link PageModel} out of DD4T's {@link Page} using the given {@link ContentProvider}.
     *
     * @param genericPage     DD4T generic page
     * @param localization    current localization
     * @param contentProvider content provider to be used for content resolving
     * @return a DXA PageModel, or null if no page builders are set
     * @throws ContentProviderException in case of problems with content provider
     */
    @Nullable <T extends PageModel> T createPageModel(Page genericPage, Localization localization, ContentProvider contentProvider) throws ContentProviderException;

    /**
     * Constructs a DXA {@link EntityModel} out of DD4T's {@link ComponentPresentation}.
     *
     * @param componentPresentation DD4T component presentation
     * @param localization          current localization
     * @return a DXA EntityModel, or null if no entity builders are set
     * @throws ContentProviderException in case of problems with content provider
     */
    @Nullable <T extends EntityModel> T createEntityModel(ComponentPresentation componentPresentation, Localization localization) throws ContentProviderException;

    /**
     * Constructs a DXA {@link EntityModel} out of DD4T's {@link Component}.
     *
     * @param component    DD4T component instance
     * @param localization current localization
     * @return a DXA EntityModel, or null if no entity builders are set
     * @throws ContentProviderException in case of problems with content provider
     */
    @Nullable <T extends EntityModel> T createEntityModel(Component component, Localization localization) throws ContentProviderException;

    /**
     * Constructs a DXA {@link EntityModel} out of DD4T's {@link Component}.
     *
     * @param component    DD4T component instance
     * @param localization current localization
     * @param entityClass  class of a DXA entity
     * @return a DXA EntityModel, or null if no entity builders are set
     * @throws ContentProviderException in case of problems with content provider
     */
    @Nullable <T extends EntityModel> T createEntityModel(Component component, Localization localization, Class<T> entityClass) throws ContentProviderException;
}

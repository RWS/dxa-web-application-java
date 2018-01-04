package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_MAPPING;
import static com.sdl.webapp.common.controller.RequestAttributeNames.ENTITY_MODEL;


/**
 * Navigation controller for the Core area.
 * <p>
 * This handles include requests to /system/mvc/Framework/Navigation/Navigation/{regionName}/{entityId}
 * and /system/mvc/Framework/Navigation/SiteMap/{regionName}/{entityId}
 * </p>
 *
 * @see ControllerUtils
 * @dxa.publicApi
 */
@Controller
@RequestMapping(INCLUDE_MAPPING + "/Navigation")
public class NavigationController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(NavigationController.class);

    private static final String NAV_TYPE_TOP = "Top";

    private static final String NAV_TYPE_LEFT = "Left";

    private static final String NAV_TYPE_BREADCRUMB = "Breadcrumb";

    private final WebRequestContext webRequestContext;

    private final NavigationProvider navigationProvider;

    /**
     * <p>Constructor for NavigationController.</p>
     *
     * @param webRequestContext  a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     * @param navigationProvider a {@link com.sdl.webapp.common.api.navigation.NavigationProvider} object.
     */
    @Autowired
    public NavigationController(WebRequestContext webRequestContext, NavigationProvider navigationProvider) {
        this.webRequestContext = webRequestContext;
        this.navigationProvider = navigationProvider;
    }

    /**
     * Changes <pre><code>
     * Home[SG]
     *  Child1[Page]
     *  Child2[SG]
     *      Item1[Page]
     *      Item2[Page]
     * </code></pre>
     * to the view needed for SiteMapXml
     * <pre><code>
     * Root[SG] - not rendered later
     *  Home[SG]
     *      Child1[Page]
     *  Child2[SG]
     *      Item1[Page]
     *      Item2[Page]</code></pre>
     *
     * @param navigationModel model to process
     */
    private static void moveHomeItemToTopLevelIfNeeded(@NotNull SitemapItem navigationModel) {
        if (!"StructureGroup".equals(navigationModel.getType())) {
            return;
        }

        Set<SitemapItem> pages = new LinkedHashSet<>();
        Set<SitemapItem> groups = new LinkedHashSet<>();

        SitemapItem home = new SitemapItem(navigationModel);
        home.setItems(pages);
        groups.add(home);

        for (SitemapItem topLevelItem : navigationModel.getItems()) {
            if ("Page".equals(topLevelItem.getType())) {
                pages.add(topLevelItem);
            } else {
                groups.add(topLevelItem);
            }
        }

        navigationModel.setItems(groups);
    }

    /**
     * Handles a request for navigation data, for example for the top navigation menu, left-side navigation or
     * breadcrumb bar.
     *
     * @param request  The request.
     * @param entityId The name of the entity.
     * @param navType  Navigation type.
     * @return The name of the entity view that should be rendered for this request.
     * @throws NavigationProviderException If an error occurs so that the navigation data cannot be retrieved.
     * @throws java.lang.Exception         if any.
     */
    @RequestMapping(value = "Navigation/{entityId}")
    public String handleGetNavigation(HttpServletRequest request,
                                      @PathVariable String entityId, @RequestParam String navType)
            throws Exception {
        LOG.trace("handleGetNavigation:, entityId={}", entityId);

        EntityModel entity = getEntityFromRequest(request, entityId);


        final String requestPath = webRequestContext.getRequestPath();
        final Localization localization = webRequestContext.getLocalization();

        NavigationLinks navigationLinks;
        switch (navType) {
            case NAV_TYPE_TOP:
                navigationLinks = navigationProvider.getTopNavigationLinks(requestPath, localization);
                break;

            case NAV_TYPE_LEFT:
                navigationLinks = navigationProvider.getContextNavigationLinks(requestPath, localization);
                break;

            case NAV_TYPE_BREADCRUMB:
                navigationLinks = navigationProvider.getBreadcrumbNavigationLinks(requestPath, localization);
                break;

            default:
                LOG.warn("Unsupported navigation type: {}", navType);
                navigationLinks = null;
                break;
        }

        final ViewModel enrichedEntity = enrichModel(entity, request);
        entity = enrichedEntity instanceof EntityModel ? (EntityModel) enrichedEntity : navigationLinks;
        request.setAttribute(ENTITY_MODEL, entity);

        if (navigationLinks != null) {
            navigationLinks.setXpmMetadata(entity.getXpmMetadata());
            navigationLinks.setXpmPropertyMetadata(entity.getXpmPropertyMetadata());
            request.setAttribute(ENTITY_MODEL, navigationLinks);
        }

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return viewNameResolver.resolveView(mvcData, "Entity");
    }

    /**
     * Handles a request to get the sitemap (in HTML).
     *
     * @param request  The request.
     * @param entityId The name of the entity.
     * @return The name of the entity view that should be rendered for this request.
     * @throws NavigationProviderException If an error occurs so that the navigation data cannot be retrieved.
     */
    @RequestMapping(value = "SiteMap/{entityId}")
    public String handleGetSiteMap(HttpServletRequest request,
                                   @PathVariable String entityId) throws NavigationProviderException {
        LOG.trace("handleGetSiteMap: entityId={}", entityId);

        final EntityModel entity = getEntityFromRequest(request, entityId);

        final SitemapItem navigationModel = navigationProvider.getNavigationModel(webRequestContext.getLocalization());

        navigationModel.setXpmMetadata(entity.getXpmMetadata());
        navigationModel.setXpmPropertyMetadata(entity.getXpmPropertyMetadata());
        moveHomeItemToTopLevelIfNeeded(navigationModel);

        suppressTopLevelEmptyItems(navigationModel);

        request.setAttribute(ENTITY_MODEL, navigationModel);

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }

    private void suppressTopLevelEmptyItems(SitemapItem navigationModel) {
        Iterator<SitemapItem> iterator = navigationModel.getItems().iterator();
        while (iterator.hasNext()) {
            SitemapItem item = iterator.next();
            if (("StructureGroup".equals(item.getType()) || item instanceof TaxonomyNode) && item.getItems().isEmpty()) {
                iterator.remove();
            }
        }
    }
}

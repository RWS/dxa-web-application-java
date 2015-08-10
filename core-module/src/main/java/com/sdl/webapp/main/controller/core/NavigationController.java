package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.NavigationProvider;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.sdl.webapp.common.controller.RequestAttributeNames.ENTITY_MODEL;
import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.*;

/**
 * Navigation controller for the Core area.
 *
 * This handles include requests to /system/mvc/Core/Navigation/Navigation/{regionName}/{entityId}
 * and /system/mvc/Core/Navigation/SiteMap/{regionName}/{entityId}
 */
@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + CORE_AREA_NAME + "/" + NAVIGATION_CONTROLLER_NAME)
public class NavigationController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(NavigationController.class);

    private static final String NAV_TYPE_TOP = "Top";
    private static final String NAV_TYPE_LEFT = "Left";
    private static final String NAV_TYPE_BREADCRUMB = "Breadcrumb";

    private final WebRequestContext webRequestContext;

    private final NavigationProvider navigationProvider;

    @Autowired
    public NavigationController(WebRequestContext webRequestContext, NavigationProvider navigationProvider) {
        this.webRequestContext = webRequestContext;
        this.navigationProvider = navigationProvider;
    }

    /**
     * Handles a request for navigation data, for example for the top navigation menu, left-side navigation or
     * breadcrumb bar.
     *
     * @param request The request.
     * @param regionName The name of the region.
     * @param entityId The name of the entity.
     * @param navType Navigation type.
     * @return The name of the entity view that should be rendered for this request.
     * @throws NavigationProviderException If an error occurs so that the navigation data cannot be retrieved.
     */
    @RequestMapping(method = RequestMethod.GET, value = NAVIGATION_ACTION_NAME + "/{regionName}/{entityId}")
    public String handleGetNavigation(HttpServletRequest request, @PathVariable String regionName,
                                      @PathVariable String entityId, @RequestParam String navType)
            throws NavigationProviderException {
        LOG.trace("handleGetNavigation: regionName={}, entityId={}", regionName, entityId);

        final Entity entity = getEntityFromRequest(request, regionName, entityId);
        request.setAttribute(ENTITY_MODEL, entity);

        final String requestPath = webRequestContext.getRequestPath();
        final Localization localization = webRequestContext.getLocalization();

        final NavigationLinks navigationLinks;
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

        if (navigationLinks != null) {
            navigationLinks.setEntityData(entity.getEntityData());
            navigationLinks.setPropertyData(entity.getPropertyData());
            request.setAttribute(ENTITY_MODEL, navigationLinks);
        }

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return resolveView(mvcData, "Entity", request);
        //return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }

    /**
     * Handles a request to get the sitemap (in HTML).
     *
     * @param request The request.
     * @param regionName The name of the region.
     * @param entityId The name of the entity.
     * @return The name of the entity view that should be rendered for this request.
     * @throws NavigationProviderException If an error occurs so that the navigation data cannot be retrieved.
     */
    @RequestMapping(method = RequestMethod.GET, value = SITEMAP_ACTION_NAME + "/{regionName}/{entityId}")
    public String handleGetSiteMap(HttpServletRequest request, @PathVariable String regionName,
                                   @PathVariable String entityId) throws NavigationProviderException {
        LOG.trace("handleGetSiteMap: regionName={}, entityId={}", regionName, entityId);

        final Entity entity = getEntityFromRequest(request, regionName, entityId);

        final SitemapItem navigationModel = navigationProvider.getNavigationModel(webRequestContext.getLocalization());
        navigationModel.setEntityData(entity.getEntityData());
        navigationModel.setPropertyData(entity.getPropertyData());
        request.setAttribute(ENTITY_MODEL, navigationModel);

        // Put all items that do not have any subitems under the "Home" item
        final List<SitemapItem> topSubItems = navigationModel.getItems();
        final List<SitemapItem> homeSubItems = new ArrayList<>();
        for (Iterator<SitemapItem> i = topSubItems.iterator(); i.hasNext(); ) {
            final SitemapItem subItem = i.next();
            if (subItem.getItems().isEmpty()) {
                i.remove();
                homeSubItems.add(subItem);
            }
        }

        final SitemapItem homeItem = new SitemapItem();
        homeItem.setTitle(navigationModel.getTitle());
        homeItem.setUrl(navigationModel.getUrl());
        homeItem.setItems(homeSubItems);

        // Add the "Home" item as the first item
        topSubItems.add(0, homeItem);

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }
}

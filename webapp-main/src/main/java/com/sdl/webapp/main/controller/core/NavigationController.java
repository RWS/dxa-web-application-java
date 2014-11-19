package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.NavigationProvider;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.main.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.main.RequestAttributeNames.ENTITY_MODEL;
import static com.sdl.webapp.main.controller.ControllerUtils.REQUEST_PATH_PREFIX;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.CORE_AREA_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.NAVIGATION_ACTION_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.NAVIGATION_CONTROLLER_NAME;

@Controller
@RequestMapping(REQUEST_PATH_PREFIX + CORE_AREA_NAME + "/" + NAVIGATION_CONTROLLER_NAME)
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
        return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }
}

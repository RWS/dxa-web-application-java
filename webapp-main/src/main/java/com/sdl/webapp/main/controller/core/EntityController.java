package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.NavigationBuilder;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.main.RequestAttributeNames.ENTITY_MODEL;
import static com.sdl.webapp.main.controller.core.AbstractController.ENTITY_PATH_PREFIX;

@Controller
@RequestMapping(ENTITY_PATH_PREFIX)
public class EntityController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    private static final String LIST_ACTION = "List";
    private static final String NAVIGATION_ACTION = "Navigation";

    private static final String NAV_TYPE_TOP = "Top";
    private static final String NAV_TYPE_LEFT = "Left";
    private static final String NAV_TYPE_BREADCRUMB = "Breadcrumb";

    private final NavigationBuilder navigationBuilder;

    @Autowired
    public EntityController(NavigationBuilder navigationBuilder) {
        this.navigationBuilder = navigationBuilder;
    }

    @RequestMapping(method = RequestMethod.GET, value = "{regionName}/{index}")
    public String handleGetEntity(HttpServletRequest request, @PathVariable String regionName, @PathVariable int index)
            throws ContentProviderException {
        LOG.trace("handleGetEntity: regionName={}, index={}", regionName, index);

        final Entity entity = getRegionFromRequest(request, regionName).getEntities().get(index);

        request.setAttribute(ENTITY_MODEL, entity);

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);

        switch (mvcData.getActionName()) {
            case LIST_ACTION:
                return handleGetList(request, entity);

            case NAVIGATION_ACTION:
                return handleGetNavigation(request, entity);

            default:
                return mvcData.getAreaName().toLowerCase() + "/entity/" + mvcData.getViewName();
        }
    }

    private String handleGetList(HttpServletRequest request, Entity entity) {
        // TODO: Not yet implemented
        final MvcData mvcData = entity.getMvcData();
        return mvcData.getAreaName().toLowerCase() + "/entity/" + mvcData.getViewName();
    }

    private String handleGetNavigation(HttpServletRequest request, Entity entity) throws ContentProviderException {
        final MvcData mvcData = entity.getMvcData();

        final NavigationLinks navigationLinks;
        final String navType = mvcData.getRouteValues().get("navType");
        switch (navType) {
            case NAV_TYPE_TOP:
                navigationLinks = navigationBuilder.buildTopNavigation();
                break;

            case NAV_TYPE_LEFT:
                navigationLinks = navigationBuilder.buildContextNavigation();
                break;

            case NAV_TYPE_BREADCRUMB:
                navigationLinks = navigationBuilder.buildBreadcrumb();
                break;

            default:
                navigationLinks = null;
                break;
        }

        if (navigationLinks != null) {
            navigationLinks.setEntityData(entity.getEntityData());
            navigationLinks.setPropertyData(entity.getPropertyData());

            request.setAttribute(ENTITY_MODEL, navigationLinks);
        }

        return mvcData.getAreaName().toLowerCase() + "/entity/" + mvcData.getViewName();
    }
}

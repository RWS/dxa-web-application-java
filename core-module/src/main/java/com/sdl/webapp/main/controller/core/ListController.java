package com.sdl.webapp.main.controller.core;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.entity.ContentList;
import com.sdl.webapp.common.controller.AbstractController;
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.RequestAttributeNames.ENTITY_MODEL;
import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.*;

/**
 * List controller for the Core area.
 *
 * This handles include requests to /system/mvc/Core/List/{regionName}/{entityId}
 */
@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + CORE_AREA_NAME + "/" + LIST_CONTROLLER_NAME)
public class ListController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(ListController.class);

    private final WebRequestContext webRequestContext;

    private final ContentProvider contentProvider;

    @Autowired
    public ListController(WebRequestContext webRequestContext, ContentProvider contentProvider) {
        this.webRequestContext = webRequestContext;
        this.contentProvider = contentProvider;
    }

    /**
     * Handles a request to fill a dynamic list with data.
     *
     * @param request The request.
     * @param regionName The region name.
     * @param entityId The entity id.
     * @return The name of the entity view that should be rendered for this request.
     */
    @RequestMapping(method = RequestMethod.GET, value = LIST_ACTION_NAME + "/{regionName}/{entityId}")
    public String handleGetList(HttpServletRequest request, @PathVariable String regionName,
                                @PathVariable String entityId) {
        LOG.trace("handleGetList: regionName={}, entityId={}", regionName, entityId);

        final Entity entity = getEntityFromRequest(request, regionName, entityId);
        request.setAttribute(ENTITY_MODEL, entity);

        if (entity instanceof ContentList) {
            final ContentList contentList = (ContentList) entity;
            if (contentList.getItemListElements().isEmpty()) {
                // we only take the start from the query string if there is also an id parameter matching the model entity id
                // this means that we are sure that the paging is coming from the right entity (if there is more than one paged list on the page)
                if (contentList.getId().equals(request.getParameter("id"))) {
                    int start = getIntParameter(request, "start", 0);
                    contentList.setCurrentPage((start / contentList.getPageSize()) + 1);
                    contentList.setStart(start);
                }

                try {
                    contentProvider.populateDynamicList(contentList, webRequestContext.getLocalization());
                } catch (ContentProviderException e) {
                    LOG.error("An unexpected error occurred", e);
                    throw new InternalServerErrorException("An unexpected error occurred", e);
                }
            }
        }

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return resolveView(mvcData, "Entity", request);
        //return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }

    private int getIntParameter(HttpServletRequest request, String parameterName, int defaultValue) {
        final String parameter = request.getParameter(parameterName);
        return !Strings.isNullOrEmpty(parameter) ? Integer.parseInt(parameter) : defaultValue;
    }
}

package com.sdl.webapp.common.controller;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.ContentList;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;

/**
 * List controller for the Core area.
 * <p>
 * This handles include requests to /system/mvc/Core/List/{regionName}/{entityId}
 * </p>
 */
@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + DefaultsMvcData.CoreAreaConstants.CORE_AREA_NAME + '/' + DefaultsMvcData.CoreAreaConstants.LIST_CONTROLLER_NAME)
public class ListController extends EntityController {
    private static final Logger LOG = LoggerFactory.getLogger(ListController.class);

    private final WebRequestContext webRequestContext;

    @Autowired
    private final ContentProvider contentProvider;

    /**
     * <p>Constructor for ListController.</p>
     *
     * @param webRequestContext a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     * @param contentProvider   a {@link com.sdl.webapp.common.api.content.ContentProvider} object.
     */
    @Autowired
    public ListController(WebRequestContext webRequestContext, ContentProvider contentProvider) {
        this.webRequestContext = webRequestContext;
        this.contentProvider = contentProvider;
    }

    private static int getIntParameter(HttpServletRequest request, String parameterName, int defaultValue) {
        final String parameter = request.getParameter(parameterName);
        return !Strings.isNullOrEmpty(parameter) ? Integer.parseInt(parameter) : defaultValue;
    }

    /**
     * Handles a request to fill a dynamic list with data.
     *
     * @param request  The request.
     * @param entityId The entity id.
     * @return The name of the entity view that should be rendered for this request.
     * @throws java.lang.Exception exception
     */
    @RequestMapping(method = RequestMethod.GET, value = DefaultsMvcData.CoreAreaConstants.LIST_ACTION_NAME + "/{entityId}")
    public String handleGetList(HttpServletRequest request, @PathVariable String entityId) throws Exception {
        LOG.trace("handleGetList: entityId={}", entityId);
        // The List action is effectively just an alias for the general Entity action (we keep it for backward compatibility).
        return handleEntityRequest(request, entityId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewModel enrichModel(ViewModel model, HttpServletRequest request) throws Exception {
        if (model instanceof ContentList) {

            final ViewModel enrichedEntity = super.enrichModel(model, request);
            final ContentList contentList = enrichedEntity instanceof EntityModel ? (ContentList) enrichedEntity : (ContentList) model;

            if (!contentList.getItemListElements().isEmpty()) {
                return model;
            }

            // we only take the start from the query string if there is also an id parameter matching the model entity id
            // this means that we are sure that the paging is coming from the right entity (if there is more than one paged list on the page)
            if (contentList.getId().equals(request.getParameter("id"))) {
                //we need to run a query to populate the list
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
        return model;
    }
}

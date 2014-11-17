package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @RequestMapping(method = RequestMethod.GET, value = "{regionName}/{index}")
    public String handleGetEntity(HttpServletRequest request, @PathVariable String regionName, @PathVariable int index) {
        LOG.trace("handleGetEntity: regionName={}, index={}", regionName, index);

        final Entity entity = getRegionFromRequest(request, regionName).getEntities().get(index);

        request.setAttribute(ENTITY_MODEL, entity);

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return mvcData.getAreaName().toLowerCase() + "/entity/" + mvcData.getViewName();
    }
}

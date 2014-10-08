package com.sdl.tridion.referenceimpl.webapp.controller.core;

import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.webapp.ViewAttributeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class EntityController extends ControllerBase {
    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/entity/{regionName}/{index}")
    public String handleGetEntity(HttpServletRequest request, @PathVariable("regionName") String regionName,
                                  @PathVariable("index") int index) {
        LOG.debug("handleGetEntity: regionName={}, index={}", regionName, index);

        final Entity entity = getRegionFromRequest(request, regionName).getEntities().get(index);

        request.setAttribute(ViewAttributeNames.ENTITY_MODEL, entity);

        return entity.getViewName();
    }
}

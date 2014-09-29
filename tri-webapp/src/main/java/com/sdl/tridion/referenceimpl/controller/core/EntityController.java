package com.sdl.tridion.referenceimpl.controller.core;

import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class EntityController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/entity/{id}")
    public String handleGetEntity(HttpServletRequest request, @PathVariable("id") String id) {
        LOG.debug("handleGetEntity: id={}", id);

        final Entity entity = getPageFromRequest(request).getEntities().get(id);
        if (entity == null) {
            LOG.error("Entity not found: {}", id);
            throw new NotFoundException("Entity not found: " + id);
        }

        request.setAttribute(ViewAttributeNames.ENTITY_MODEL, entity);

        return entity.getViewName();
    }
}

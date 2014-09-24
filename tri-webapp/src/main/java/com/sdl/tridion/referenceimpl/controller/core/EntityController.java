package com.sdl.tridion.referenceimpl.controller.core;

import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class EntityController {
    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/entity")
    public String handleGetEntity(HttpServletRequest request) {
        final Entity entity = getEntity(request);
        LOG.debug("handleGetEntity: entity={}", entity);

        return entity.getViewName();
    }

    private Entity getEntity(HttpServletRequest request) {
        final Entity entity = (Entity) request.getAttribute(JspBeanNames.ENTITY_MODEL);
        if (entity == null) {
            LOG.error("Entity not found in request attributes");
            throw new NotFoundException("Entity not found in request attributes");
        }

        return entity;
    }
}

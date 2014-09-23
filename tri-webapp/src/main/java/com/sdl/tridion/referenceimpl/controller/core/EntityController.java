package com.sdl.tridion.referenceimpl.controller.core;

import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.controller.exception.ForbiddenException;
import com.sdl.tridion.referenceimpl.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class EntityController {
    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    private static final String ENTITY_VIEW_PREFIX = "core/entity/";

    @RequestMapping(method = RequestMethod.GET, value = "/entity/{id}")
    public String handleGetEntity(HttpServletRequest request, @PathVariable("id") String id) {
        LOG.debug("handleGetEntity: id={}", id);

        final Page pageModel = (Page) request.getAttribute(JspBeanNames.PAGE_MODEL);
        if (pageModel == null) {
            LOG.warn("Access to entity without page model: {}", id);
            throw new ForbiddenException("Access to entity without page model");
        }

        final Entity entityModel = pageModel.getEntity(id);
        if (entityModel == null) {
            LOG.warn("Entity not found: {} for page: {}", id, pageModel.getId());
            throw new NotFoundException("Entity not found: " + id + " for page: " + pageModel.getId());
        }

        request.setAttribute(JspBeanNames.ENTITY_MODEL, entityModel);

        return ENTITY_VIEW_PREFIX + entityModel.getViewName();
    }
}
